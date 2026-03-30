package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.EmployeeClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmployeeClientFallbackFactory implements FallbackFactory<EmployeeClient> {

  @Override
  public EmployeeClient create(Throwable cause) {
    log.error("Feign调用EmployeeClient失败", cause);
    return new EmployeeClient() {
    };
  }
}