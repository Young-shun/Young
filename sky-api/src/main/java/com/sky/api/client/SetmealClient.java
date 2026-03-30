package com.sky.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.api.client.fallback.SetmealClientFallbackFactory;
import com.sky.entity.Setmeal;
import com.sky.entity.Category;
import com.sky.result.Result;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sky-take-out-setmeal", fallbackFactory = SetmealClientFallbackFactory.class)
public interface SetmealClient {
  // Feign should call a lightweight endpoint that returns Setmeal entity
  @GetMapping("/inner/setmeal/{id}")
  /**
   * 作用: 执行getById相关逻辑。
   * 输入: @PathVariable("id") Long id。
   * 输出: Setmeal。
   */
  Setmeal getById(@PathVariable("id") Long id);

  @GetMapping("/inner/setmeal/count")
  Result<Long> countByStatus(@RequestParam("status") Integer status);

  @PostMapping("/inner/setmeal/category/sync")
  Result<String> syncCategory(@RequestBody Category category);

  @DeleteMapping("/inner/setmeal/category/{id}")
  Result<String> deleteCategory(@PathVariable("id") Long id);
}
