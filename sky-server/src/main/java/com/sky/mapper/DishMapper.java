package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    /**
     * 动态条件查询菜品
     * 
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 分页查询菜品
     * 
     * @param page, @Param("dto") DishPageQueryDTO dto
     * @return
     */
    Page<DishVO> page(Page<DishVO> page, @Param("dto") DishPageQueryDTO dto);

}
