package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import com.sky.api.client.fallback.CategoryClientFallbackFactory;
import com.sky.entity.Category;

@FeignClient(name = "sky-take-out-category", fallbackFactory = CategoryClientFallbackFactory.class)
public interface CategoryClient {
  @GetMapping("/user/category/list")
  List<Category> list(@RequestParam(required = false) Integer type);
}
