package com.sky.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.gateway.client.fallback.CategoryClientFallbackFactory;

import java.util.List;

@FeignClient(name = "sky-take-out-category", fallbackFactory = CategoryClientFallbackFactory.class)
public interface CategoryClient {
  @GetMapping("/user/category/list")
  List<Object> list(@RequestParam(required = false) Integer type);
}
