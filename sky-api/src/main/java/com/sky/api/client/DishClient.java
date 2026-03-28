package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.entity.Dish;
import com.sky.entity.Category;
import com.sky.result.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sky-take-out-dish")
public interface DishClient {
  // Feign should call a lightweight endpoint that returns Dish entity
  @GetMapping("/inner/dish/{id}")
  Dish getById(@PathVariable("id") Long id);

  @GetMapping("/inner/dish/count")
  Result<Long> countByStatus(@RequestParam("status") Integer status);

  @PostMapping("/inner/dish/category/sync")
  Result<String> syncCategory(@RequestBody Category category);

  @DeleteMapping("/inner/dish/category/{id}")
  Result<String> deleteCategory(@PathVariable("id") Long id);
}
