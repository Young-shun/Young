package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sky-take-out-shop")
public interface ShopClient {
  // add RPC methods as needed
}
