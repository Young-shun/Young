package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.entity.Setmeal;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    List<Setmeal> list(Setmeal setmeal);

    java.util.List<com.sky.vo.DishItemVO> getDishItemBySetmealId(Long id);

    Page<SetmealVO> pageQuery(@Param("page") Page<SetmealVO> page,
            @Param("dto") SetmealPageQueryDTO queryDTO);
}
