package com.sky.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sky.context.BaseContext;

import feign.RequestInterceptor;

@Configuration
public class FeignUserInfoConfig {

  @Bean
  public RequestInterceptor userInfoForwardInterceptor() {
    return template -> {
      Long userId = BaseContext.getCurrentId();
      if (userId != null) {
        template.header("userInfo", String.valueOf(userId));
      }
    };
  }
}
