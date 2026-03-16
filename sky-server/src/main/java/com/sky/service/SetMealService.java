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

  /**
   * 条件查询
   * 
   * @param setmeal
   * @return
   */
  List<Setmeal> list(Setmeal setmeal);

  /**
   * 根据id查询菜品选项
   * 
   * @param id
   * @return
   */
  List<DishItemVO> getDishItemById(Long id);

  /**
   * 分页查询
   * 
   * @return
   */
  Page<SetmealVO> querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO);

}
