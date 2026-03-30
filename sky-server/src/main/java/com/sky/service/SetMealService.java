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

  /**
   * 作用: 执行createSetMeal相关逻辑。
   * 输入: SetmealDTO setMealDto。
   * 输出: 无。
   */
  void createSetMeal(SetmealDTO setMealDto);

  /**
   * 作用: 执行delete相关逻辑。
   * 输入: List<Long> ids。
   * 输出: 无。
   */
  void delete(List<Long> ids);

  /**
   * 作用: 执行startOrStop相关逻辑。
   * 输入: Integer status, Long id。
   * 输出: 无。
   */
  void startOrStop(Integer status, Long id);

  /**
   * 作用: 执行getByIdWithDish相关逻辑。
   * 输入: Long id。
   * 输出: SetmealVO。
   */
  SetmealVO getByIdWithDish(Long id);

  /**
   * 作用: 执行updateWithDish相关逻辑。
   * 输入: SetmealDTO setmealDTO。
   * 输出: 无。
   */
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
