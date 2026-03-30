package com.sky.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.json.JacksonObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一查询缓存切面: 先查 Redis, 未命中再执行数据库查询并回填缓存。
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class QueryRedisCacheAspect {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private static final Set<String> QUERY_PREFIXES = new HashSet<>(Arrays.asList(
            "get", "list", "page", "query", "find", "search", "count", "statistics", "top", "turn"));

    private static final Set<String> WRITE_PREFIXES = new HashSet<>(Arrays.asList(
            "save", "add", "insert", "create", "update", "modify", "delete", "remove", "clean", "start",
            "stop", "pay", "cancel", "confirm", "reject", "rejection", "deliver", "delivery", "complete",
            "submit", "login", "logout", "export", "set", "reminder", "repetition"));

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new JacksonObjectMapper();

    @Value("${spring.application.name:unknown-service}")
    private String applicationName;

    @Around("execution(public * com.sky.service.impl..*(..))")
    public Object cacheQueryResult(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (!shouldCache(method)) {
            return joinPoint.proceed();
        }

        String cacheKey = buildCacheKey(joinPoint, method);

        try {
            String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null && !cachedJson.isEmpty()) {
                JavaType returnType = buildReturnJavaType(method);
                return objectMapper.readValue(cachedJson, returnType);
            }
        } catch (Exception ex) {
            log.warn("读取 Redis 缓存失败, key={}, error={}", cacheKey, ex.getMessage());
        }

        Object result = joinPoint.proceed();
        if (result == null) {
            return null;
        }

        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
        } catch (Exception ex) {
            log.warn("写入 Redis 缓存失败, key={}, error={}", cacheKey, ex.getMessage());
        }

        return result;
    }

    private boolean shouldCache(Method method) {
        String methodName = method.getName();

        if (method.getReturnType().equals(Void.TYPE)) {
            return false;
        }

        String lowerName = methodName.toLowerCase();

        for (String writePrefix : WRITE_PREFIXES) {
            if (lowerName.startsWith(writePrefix)) {
                return false;
            }
        }

        for (String queryPrefix : QUERY_PREFIXES) {
            if (lowerName.startsWith(queryPrefix)) {
                return true;
            }
        }

        return lowerName.contains("bypage") || lowerName.contains("detail") || lowerName.contains("statistics");
    }

    private JavaType buildReturnJavaType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        return objectMapper.getTypeFactory().constructType(genericReturnType);
    }

    private String buildCacheKey(ProceedingJoinPoint joinPoint, Method method) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String argsJson;

        try {
            argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
        } catch (Exception ex) {
            argsJson = Arrays.toString(joinPoint.getArgs());
        }

        String argsHash = DigestUtils.md5DigestAsHex(argsJson.getBytes(StandardCharsets.UTF_8));
        return "query_cache:" + applicationName + ":" + className + ":" + methodName + ":" + argsHash;
    }
}
