package com.sky.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.user.client.fallback.CategoryFeignClientFallbackFactory;

import java.util.List;

@FeignClient(name = "sky-take-out-category", fallbackFactory = CategoryFeignClientFallbackFactory.class)
public interface CategoryFeignClient {
  @GetMapping("/user/category/list")
  List<Object> list(@RequestParam(required = false) Integer type);
}
