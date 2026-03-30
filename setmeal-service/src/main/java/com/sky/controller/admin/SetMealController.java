package com.sky.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.PageDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.api.client.CategoryClient;
import com.sky.vo.SetmealVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {
  @Autowired
  private SetMealService setMealService;

  @Autowired
  private CategoryClient categoryClient;
  @Autowired
  private RedisTemplate redisTemplate;

  @PostMapping
  @CacheEvict(cacheNames = "Setmeals", key = "#setMealDto.categoryId")
  /**
   * 作用: 执行createSetMeal相关逻辑。
   * 输入: @RequestBody SetmealDTO setMealDto。
   * 输出: Result<String>。
   */
  public Result<String> createSetMeal(@RequestBody SetmealDTO setMealDto) {
    log.info("Creating set meal: {}", setMealDto);
    setMealService.createSetMeal(setMealDto);
    return Result.success();
  }

  @GetMapping("/page")
  /**
   * 作用: 执行page相关逻辑。
   * 输入: SetmealPageQueryDTO setmealPageQueryDTO。
   * 输出: Result<PageResult>。
   */
  public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
    log.info("套餐分页查询，参数：{}", setmealPageQueryDTO);
    Page<SetmealVO> p = setMealService.querySetmealByPage(setmealPageQueryDTO);

    return Result.success(new PageResult(p.getTotal(), p.getRecords()));
  }

  @DeleteMapping
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  /**
   * 作用: 执行delete相关逻辑。
   * 输入: @RequestParam List<Long> ids。
   * 输出: Result<String>。
   */
  public Result<String> delete(@RequestParam List<Long> ids) {
    log.info("删除套餐，id：{}", ids);
    setMealService.delete(ids);
    return Result.success();
  }

  @GetMapping("/{id}")
  /**
   * 作用: 执行getById相关逻辑。
   * 输入: @PathVariable Long id。
   * 输出: Result<SetmealVO>。
   */
  public Result<SetmealVO> getById(@PathVariable Long id) {
    SetmealVO setmeal = setMealService.getByIdWithDish(id);
    return Result.success(setmeal);
  }

  // Lightweight endpoint for Feign clients to fetch raw Setmeal entity
  @GetMapping("/entity/{id}")
  /**
   * 作用: 执行entityById相关逻辑。
   * 输入: @PathVariable Long id。
   * 输出: Setmeal。
   */
  public Setmeal entityById(@PathVariable Long id) {
    return setMealService.getByIdEntity(id);
  }

  @PutMapping
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  /**
   * 作用: 执行update相关逻辑。
   * 输入: @RequestBody SetmealDTO setmealDTO。
   * 输出: Result<String>。
   */
  public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
    log.info("更新套餐，参数：{}", setmealDTO);
    setMealService.updateWithDish(setmealDTO);
    return Result.success();
  }

  @PostMapping("/status/{status}")
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  /**
   * 作用: 执行startOrStop相关逻辑。
   * 输入: @PathVariable("status") Integer status, Long id。
   * 输出: Result<String>。
   */
  public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
    setMealService.startOrStop(status, id);
    return Result.success();
  }
}
