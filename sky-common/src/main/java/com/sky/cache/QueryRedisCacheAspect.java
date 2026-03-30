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
import com.sky.context.BaseContext;
import com.sky.json.JacksonObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一查询缓存切面: 先查 Redis, 未命中再执行数据库查询并回填缓存。
 *
 * 设计目标:
 * 1. 统一在切面层做查询缓存，避免在各个业务方法中重复写缓存代码。
 * 2. 对查询路径做“Cache-Aside(旁路缓存)”模式:
 * - 先读缓存
 * - 未命中再查数据库
 * - 回填缓存并设置过期时间
 * 3. 写操作不进入缓存逻辑，避免误缓存脏数据。
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class QueryRedisCacheAspect {

  /**
   * 查询结果缓存 10 分钟。
   */
  private static final Duration CACHE_TTL = Duration.ofMinutes(10);

  /**
   * 方法名前缀命中以下集合时，视为“查询方法”。
   */
  private static final Set<String> QUERY_PREFIXES = new HashSet<>(Arrays.asList(
      "get", "list", "page", "query", "find", "search", "count", "statistics", "top", "turn"));

  /**
   * 方法名前缀命中以下集合时，视为“写操作方法”，直接跳过缓存。
   */
  private static final Set<String> WRITE_PREFIXES = new HashSet<>(Arrays.asList(
      "save", "add", "insert", "create", "update", "modify", "delete", "remove", "clean", "start",
      "stop", "pay", "cancel", "confirm", "reject", "rejection", "deliver", "delivery", "complete",
      "submit", "login", "logout", "export", "set", "reminder", "repetition"));

  /**
   * 使用 StringRedisTemplate 统一读写字符串，value 序列化由 ObjectMapper 处理。
   */
  @Resource
  private StringRedisTemplate stringRedisTemplate;

  /**
   * 统一使用项目内的 JacksonObjectMapper，保持时间等类型序列化风格一致。
   */
  private final ObjectMapper objectMapper = new JacksonObjectMapper();

  /**
   * 当前应用名，作为缓存 key 的一部分，隔离不同服务的同名类/方法冲突。
   */
  @Value("${spring.application.name:unknown-service}")
  private String applicationName;

  /**
   * 核心环绕通知:
   * 1. 识别是否需要缓存。
   * 2. 需要缓存则先查 Redis。
   * 3. 未命中执行原方法查询。
   * 4. 将查询结果回填到 Redis，并设置过期时间。
   *
   * 拦截范围:
   * - service.impl 层 public 方法
   * - controller 层 public 方法
   */
  @Around("execution(public * com.sky.service.impl..*(..)) || execution(public * com.sky.controller..*(..))")
  public Object cacheQueryResult(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // 写操作: 先执行业务(落库)再清理缓存，确保最终一致性。
    if (isWriteMethod(method)) {
      Object result = joinPoint.proceed();
      evictServiceCache();
      return result;
    }

    // 非查询方法(或写方法)直接放行，不走缓存逻辑。
    if (!shouldCache(method)) {
      return joinPoint.proceed();
    }

    // 为“类+方法+参数”构建稳定缓存 key。
    String cacheKey = buildCacheKey(joinPoint, method);

    try {
      // Step 1: 先读缓存，命中则直接返回。
      String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
      if (cachedJson != null && !cachedJson.isEmpty()) {
        JavaType returnType = buildReturnJavaType(method);
        return objectMapper.readValue(cachedJson, returnType);
      }
    } catch (Exception ex) {
      log.warn("读取 Redis 缓存失败, key={}, error={}", cacheKey, ex.getMessage());
    }

    Object result = joinPoint.proceed();

    // 查询结果为 null 时不缓存，避免把“空值”长期写入。
    if (result == null) {
      return null;
    }

    try {
      // Step 2: 回填缓存，后续相同请求可直接命中。
      String json = objectMapper.writeValueAsString(result);
      stringRedisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
    } catch (Exception ex) {
      log.warn("写入 Redis 缓存失败, key={}, error={}", cacheKey, ex.getMessage());
    }
    return result;
  }

  /**
   * 判断方法是否应走缓存。
   *
   * 判定规则:
   * 1. 返回值为 void -> 直接判定为不缓存。
   * 2. 方法名前缀命中 WRITE_PREFIXES -> 不缓存。
   * 3. 方法名前缀命中 QUERY_PREFIXES -> 缓存。
   * 4. 方法名包含一些常见查询关键字 -> 缓存。
   */
  private boolean shouldCache(Method method) {
    String methodName = method.getName();

    // void 方法通常是命令式操作，不参与缓存。
    if (method.getReturnType().equals(Void.TYPE)) {
      return false;
    }

    String lowerName = methodName.toLowerCase();

    // 写操作前缀优先级最高，优先排除。
    for (String writePrefix : WRITE_PREFIXES) {
      if (lowerName.startsWith(writePrefix)) {
        return false;
      }
    }

    // 标准查询前缀直接判定为可缓存。
    for (String queryPrefix : QUERY_PREFIXES) {
      if (lowerName.startsWith(queryPrefix)) {
        return true;
      }
    }

    // 兜底关键字匹配，覆盖 byPage/detail/统计类命名。
    return lowerName.contains("bypage")
        || lowerName.contains("detail")
        || lowerName.contains("statistics")
        || lowerName.contains("count")
        || lowerName.contains("overview")
        || lowerName.contains("turnover")
        || lowerName.contains("top10");
  }

  /**
   * 判断方法是否属于写操作。
   */
  private boolean isWriteMethod(Method method) {
    String lowerName = method.getName().toLowerCase();
    for (String writePrefix : WRITE_PREFIXES) {
      if (lowerName.startsWith(writePrefix)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 清理当前服务下的查询缓存。
   *
   * 说明:
   * 1. 写操作后统一失效当前服务缓存，避免读到旧数据。
   * 2. 仅删除 query_cache:{applicationName}:* 前缀，不影响其它服务。
   */
  private void evictServiceCache() {
    String pattern = "query_cache:" + applicationName + ":*";
    try {
      Set<String> keys = stringRedisTemplate.keys(pattern);
      if (keys != null && !keys.isEmpty()) {
        Long deleted = stringRedisTemplate.delete(keys);
        log.info("写操作后清理缓存完成, pattern={}, deleted={}", pattern, deleted == null ? 0 : deleted);
      }
    } catch (Exception ex) {
      log.warn("写操作后清理缓存失败, pattern={}, error={}", pattern, ex.getMessage());
    }
  }

  /**
   * 根据方法的泛型返回值构建 Jackson 的 JavaType。
   * 例如可正确处理 List<T>、Result<Page<T>> 等泛型结构。
   */
  private JavaType buildReturnJavaType(Method method) {
    Type genericReturnType = method.getGenericReturnType();
    return objectMapper.getTypeFactory().constructType(genericReturnType);
  }

  /**
   * 生成缓存 key。
   *
   * key 结构:
   * query_cache:{applicationName}:{userId}:{className}:{methodName}:{argsHash}
   *
   * 说明:
   * 1. 同服务不同方法天然隔离。
   * 2. 同用户同方法不同参数通过 argsHash 区分。
   * 3. 增加 userId 维度，避免不同用户共享同一份查询缓存。
   * 4. 使用 MD5 压缩参数，避免 key 过长。
   */
  private String buildCacheKey(ProceedingJoinPoint joinPoint, Method method) {
    Long currentUserId = BaseContext.getCurrentId();
    String userId = currentUserId == null ? "anonymous" : String.valueOf(currentUserId);
    String className = joinPoint.getTarget().getClass().getName();
    String methodName = method.getName();
    String argsJson;

    try {
      // 优先使用 JSON 形式，保证同值参数序列化稳定。
      argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
    } catch (Exception ex) {
      // JSON 序列化失败时，降级到 Arrays.toString，避免中断主流程。
      argsJson = Arrays.toString(joinPoint.getArgs());
    }

    String argsHash = DigestUtils.md5DigestAsHex(argsJson.getBytes(StandardCharsets.UTF_8));
    return "query_cache:" + applicationName + ":" + userId + ":" + className + ":" + methodName + ":" + argsHash;
  }
}
