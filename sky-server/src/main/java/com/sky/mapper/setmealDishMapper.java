package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.SetmealDish;

@Mapper
public interface setmealDishMapper {

  /**
   * 根据菜品id查询套餐的数量
   * 
   * @param dishId
   * @return
   */
  @Select("select count(id) from setmeal_dish where dish_id = #{dishId}")
  Integer countByDishId(Long dishId);

  /**
   * 批量插入菜品口味
   * 
   * @param dishs
   */
  void insertBatch(List<SetmealDish> dishs);

  void deleteBySetmealIds(List<Long> ids);

  @Select("select * from setmeal_dish where setmeal_id = #{id}")
  List<SetmealDish> selectBySetmealId(Long id);

}
