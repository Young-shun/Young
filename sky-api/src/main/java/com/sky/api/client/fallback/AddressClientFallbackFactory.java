package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.AddressClient;
import com.sky.entity.AddressBook;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AddressClientFallbackFactory implements FallbackFactory<AddressClient> {

  @Override
  public AddressClient create(Throwable cause) {
    return id -> {
      log.error("Feign调用AddressClient#getById失败, id={}", id, cause);
      return Result.error("地址服务暂时不可用");
    };
  }
}