package com.sky.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

public interface DishService extends IService<Dish> {

  /**
   * 作用: 执行saveWithFlavor相关逻辑。
   * 输入: DishDTO dishDTO。
   * 输出: 无。
   */
  void saveWithFlavor(DishDTO dishDTO);

  /**
   * 作用: 执行delete相关逻辑。
   * 输入: List<Long> ids。
   * 输出: 无。
   */
  void delete(List<Long> ids);

  /**
   * 作用: 执行getByIdWithFlavor相关逻辑。
   * 输入: Long id。
   * 输出: DishVO。
   */
  DishVO getByIdWithFlavor(Long id);

  /**
   * 作用: 执行updateWithFlavor相关逻辑。
   * 输入: DishDTO dishDTO。
   * 输出: 无。
   */
  void updateWithFlavor(DishDTO dishDTO);

  /**
   * 作用: 执行startOrStop相关逻辑。
   * 输入: Integer status, Long id。
   * 输出: 无。
   */
  void startOrStop(Integer status, Long id);

  List<Dish> list(Long categoryId);

  List<DishVO> listWithFlavor(Dish dish);

  Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO);

  // return raw Dish entity for RPC clients
  /**
   * 作用: 执行getByIdEntity相关逻辑。
   * 输入: Long id。
   * 输出: Dish。
   */
  Dish getByIdEntity(Long id);

}
