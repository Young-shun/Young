package com.sky.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

public interface SetMealService extends IService<Setmeal> {

  void createSetMeal(SetmealDTO setMealDto);

  void delete(List<Long> ids);

  void startOrStop(Integer status, Long id);

  SetmealVO getByIdWithDish(Long id);

  void updateWithDish(SetmealDTO setmealDTO);

  List<Setmeal> list(Setmeal setmeal);

  List<DishItemVO> getDishItemById(Long id);

  Page<SetmealVO> querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO);

  // return raw Setmeal entity for RPC clients
  Setmeal getByIdEntity(Long id);

}
