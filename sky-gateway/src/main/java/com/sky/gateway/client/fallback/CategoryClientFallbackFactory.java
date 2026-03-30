package com.sky.gateway.client.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.gateway.client.CategoryClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CategoryClientFallbackFactory implements FallbackFactory<CategoryClient> {

  @Override
  public CategoryClient create(Throwable cause) {
    return type -> {
      log.error("Feign调用Gateway CategoryClient#list失败, type={}", type, cause);
      return Collections.emptyList();
    };
  }
}
