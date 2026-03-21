package com.sky.controller.admin;

import java.util.List;
import java.util.Set;

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
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetMealController {
  @Autowired
  private SetMealService setMealService;
  @Autowired
  private RedisTemplate redisTemplate;

  /**
   * 新增套餐
   * 
   * @param setMealDto
   * @return
   */
  @PostMapping
  @CacheEvict(cacheNames = "Setmeals", key = "#setMealDto.categoryId")
  public Result<String> createSetMeal(@RequestBody SetmealDTO setMealDto) {
    log.info("Creating set meal: {}", setMealDto);
    setMealService.createSetMeal(setMealDto);
    return Result.success();
  }

  /**
   * 套餐分页查询
   * 
   * @param setmealPageQueryDTO
   * @return
   */
  @GetMapping("/page")
  public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
    log.info("套餐分页查询，参数：{}", setmealPageQueryDTO);
    Page<Setmeal> p = setMealService
        .page(new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize()));

    return Result.success(new PageResult(p.getTotal(), p.getRecords()));
  }

  /**
   * 删除菜品
   * 
   * @param id
   * @return
   */
  @DeleteMapping
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  public Result<String> delete(@RequestParam List<Long> ids) {
    log.info("删除套餐，id：{}", ids);
    setMealService.delete(ids);
    return Result.success();
  }

  /**
   * 根据id查询套餐
   * 
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public Result<SetmealVO> getById(@PathVariable Long id) {
    SetmealVO setmeal = setMealService.getByIdWithDish(id);
    return Result.success(setmeal);
  }

  /**
   * 更新套餐
   * 
   * @param dishDTO
   * @return
   */
  @PutMapping
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
    log.info("更新套餐，参数：{}", setmealDTO);
    setMealService.updateWithDish(setmealDTO);
    return Result.success();
  }

  /**
   * 启用、禁用分类
   * 
   * @param status
   * @param id
   * @return
   */
  @PostMapping("/status/{status}")
  @CacheEvict(cacheNames = "Setmeals", allEntries = true)
  public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
    setMealService.startOrStop(status, id);
    return Result.success();
  }
}
