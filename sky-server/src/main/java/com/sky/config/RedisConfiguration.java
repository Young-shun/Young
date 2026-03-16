package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类，用于创建RedisTemplate对象
 */
@Configuration
@Slf4j
public class RedisConfiguration {

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    log.info("创建RedisTemplate对象...");
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    // 设置redis连接工厂
    template.setConnectionFactory(redisConnectionFactory);
    // 序列化配置
    template.setKeySerializer(new StringRedisSerializer());
    return template;
  }

}
