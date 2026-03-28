package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.entity.Dish;
import com.sky.dto.DishPageQueryDTO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    List<Dish> list(Dish dish);

    Page<DishVO> page(@Param("page") Page<DishVO> page, @Param("dto") DishPageQueryDTO queryDTO);
}
