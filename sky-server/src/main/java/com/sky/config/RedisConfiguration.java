package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类，用于创建RedisTemplate对象
 */
@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "sky.common", name = "enable-redis", havingValue = "true", matchIfMissing = false)
public class RedisConfiguration {
  @Bean
  @ConditionalOnMissingBean(RedisTemplate.class)
  /**
   * 作用: 执行redisTemplate相关逻辑。
   * 输入: RedisConnectionFactory redisConnectionFactory。
   * 输出: RedisTemplate<Object, Object>。
   */
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    log.info("创建RedisTemplate对象...");
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    // 设置redis连接工厂
    template.setConnectionFactory(redisConnectionFactory);
    // 序列化配置
    template.setKeySerializer(new StringRedisSerializer());

    // Set value serializer to Jackson2JsonRedisSerializer
    PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    valueSerializer.setObjectMapper(objectMapper);
    template.setValueSerializer(valueSerializer);

    return template;
  }

}
