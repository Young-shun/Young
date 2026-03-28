package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import com.sky.entity.Category;

@FeignClient(name = "sky-take-out-category")
public interface CategoryClient {
  @GetMapping("/user/category/list")
  List<Category> list(@RequestParam(required = false) Integer type);
}
