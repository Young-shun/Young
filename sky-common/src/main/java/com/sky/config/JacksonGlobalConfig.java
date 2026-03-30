package com.sky.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

@Configuration
public class JacksonGlobalConfig {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter DATE_TIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

  @Bean
  /**
   * 作用: 执行jackson2ObjectMapperBuilderCustomizer相关逻辑。
   * 输入: 无。
   * 输出: Jackson2ObjectMapperBuilderCustomizer。
   */
  public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
    return builder -> builder
        .deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
          @Override
          public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String raw = p.getText();
            if (raw == null || raw.trim().isEmpty()) {
              return null;
            }
            String value = raw.trim();
            try {
              return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ex) {
              return LocalDateTime.parse(value, DATE_TIME_MINUTE_FORMATTER);
            }
          }
        })
        .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER))
        .serializerByType(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER))
        .serializerByType(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
  }
}
