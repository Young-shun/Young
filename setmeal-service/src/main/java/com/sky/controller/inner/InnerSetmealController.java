package com.sky.controller.inner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;

@RestController
@RequestMapping("/inner/setmeal")
public class InnerSetmealController {

  @Autowired
  private SetMealService setMealService;

  @GetMapping("/{id}")
  /**
   * 作用: 执行getById相关逻辑。
   * 输入: @PathVariable("id") Long id。
   * 输出: Setmeal。
   */
  public Setmeal getById(@PathVariable("id") Long id) {
    return setMealService.getById(id);
  }

  @GetMapping("/count")
  /**
   * 作用: 执行countByStatus相关逻辑。
   * 输入: @RequestParam("status") Integer status。
   * 输出: Result<Long>。
   */
  public Result<Long> countByStatus(@RequestParam("status") Integer status) {
    long count = setMealService.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getStatus, status));
    return Result.success(count);
  }
}
