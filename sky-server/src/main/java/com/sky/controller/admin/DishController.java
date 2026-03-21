package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
  @Autowired
  private DishService dishService;
  @Autowired
  private RedisTemplate redisTemplate;

  /**
   * 新增菜品
   * 
   * @param dishDTO
   * @return
   */
  @PostMapping
  @CacheEvict(cacheNames = "Dishes_", key = "#dishDTO.categoryId")
  public Result<String> save(@RequestBody DishDTO dishDTO) {
    log.info("新增菜品，参数：{}", dishDTO);
    dishService.saveWithFlavor(dishDTO);
    return Result.success();
  }

  /**
   * 菜品分页查询
   * 
   * @param dishPageQueryDTO
   * @return
   */
  @GetMapping("/page")
  public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
    log.info("菜品分页查询，参数：{}", dishPageQueryDTO);
    Page<Dish> p = dishService
        .page(new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize()));

    return Result.success(new PageResult(p.getTotal(), p.getRecords()));
  }

  /**
   * 删除菜品
   * 
   * @param id
   * @return
   */
  @DeleteMapping
  @CacheEvict(cacheNames = "Dishes_", allEntries = true)
  public Result<String> delete(@RequestParam List<Long> ids) {
    log.info("删除菜品，id：{}", ids);
    dishService.delete(ids);
    return Result.success();
  }

  /**
   * 根据id查询菜品
   * 
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public Result<DishVO> getById(@PathVariable Long id) {
    DishVO dish = dishService.getByIdWithFlavor(id);
    return Result.success(dish);
  }

  /**
   * 更新菜品
   * 
   * @param dishDTO
   * @return
   */
  @PutMapping
  @CacheEvict(cacheNames = "Dishes_", allEntries = true)
  public Result<String> update(@RequestBody DishDTO dishDTO) {
    log.info("更新菜品，参数：{}", dishDTO);
    dishService.updateWithFlavor(dishDTO);
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
  @CacheEvict(cacheNames = "Dishes_", allEntries = true)
  public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
    dishService.startOrStop(status, id);
    return Result.success();
  }

  /**
   * 根据分类id查询菜品
   * 
   * @param categoryId
   * @return
   */
  @GetMapping("/list")
  @ApiOperation("根据分类id查询菜品")
  public Result<List<Dish>> list(Long categoryId) {
    List<Dish> list = dishService.list(categoryId);
    return Result.success(list);
  }
}