package com.sky.dish.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

@Configuration
@Slf4j
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(prefix = "sky.common", name = "enable-redis", havingValue = "true", matchIfMissing = false)
public class DishRedisConfig {

  @Bean
  @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(RedisTemplate.class)
  /**
   * 作用: 执行redisTemplate相关逻辑。
   * 输入: RedisConnectionFactory redisConnectionFactory。
   * 输出: RedisTemplate<String, Object>。
   */
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    log.info("[Dish] 创建本地 RedisTemplate 对象...");
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());

    PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
    Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    valueSerializer.setObjectMapper(objectMapper);
    template.setValueSerializer(valueSerializer);

    return template;
  }

}
