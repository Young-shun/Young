package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.ShopClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShopClientFallbackFactory implements FallbackFactory<ShopClient> {

  @Override
  public ShopClient create(Throwable cause) {
    log.error("Feign调用ShopClient失败", cause);
    return new ShopClient() {
    };
  }
}