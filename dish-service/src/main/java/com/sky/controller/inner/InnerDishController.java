package com.sky.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;

@RestController
@RequestMapping("/inner/dish")
public class InnerDishController {

  @Autowired
  private DishService dishService;

  @GetMapping("/{id}")
  /**
   * 作用: 执行getById相关逻辑。
   * 输入: @PathVariable("id") Long id。
   * 输出: Dish。
   */
  public Dish getById(@PathVariable("id") Long id) {
    return dishService.getByIdEntity(id);
  }

  @GetMapping("/count")
  /**
   * 作用: 执行countByStatus相关逻辑。
   * 输入: @RequestParam("status") Integer status。
   * 输出: Result<Long>。
   */
  public Result<Long> countByStatus(@RequestParam("status") Integer status) {
    long count = dishService.count(new LambdaQueryWrapper<Dish>().eq(Dish::getStatus, status));
    return Result.success(count);
  }
}
