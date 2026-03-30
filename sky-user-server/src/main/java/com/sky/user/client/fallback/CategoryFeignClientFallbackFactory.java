package com.sky.user.client.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.user.client.CategoryFeignClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CategoryFeignClientFallbackFactory implements FallbackFactory<CategoryFeignClient> {

  @Override
  public CategoryFeignClient create(Throwable cause) {
    return type -> {
      log.error("Feign调用CategoryFeignClient#list失败, type={}", type, cause);
      return Collections.emptyList();
    };
  }
}
