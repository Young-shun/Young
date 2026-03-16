package com.sky.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

public interface DishService extends IService<Dish> {

  void saveWithFlavor(DishDTO dishDTO);

  void delete(List<Long> ids);

  DishVO getByIdWithFlavor(Long id);

  void updateWithFlavor(DishDTO dishDTO);

  void startOrStop(Integer status, Long id);

  List<Dish> list(Long categoryId);

  /**
   * 条件查询菜品和口味
   * 
   * @param dish
   * @return
   */
  List<DishVO> listWithFlavor(Dish dish);

  Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO);

}
