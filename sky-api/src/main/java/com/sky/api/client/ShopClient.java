package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import com.sky.api.client.fallback.ShopClientFallbackFactory;

@FeignClient(name = "sky-take-out-shop", fallbackFactory = ShopClientFallbackFactory.class)
public interface ShopClient {
  // add RPC methods as needed
}
