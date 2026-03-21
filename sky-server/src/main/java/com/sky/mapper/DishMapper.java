package com.sky.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import com.sky.notation.AutoFill;
import com.sky.vo.DishVO;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据分类id查询菜品数量
     * 
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据id查询菜品
     */
    @Select("select * from dish where id = #{id}")
    Dish selectById(Long id);

    /**
     * 根据id查询菜品和口味
     * 
     * @param id
     * @return
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除菜品
     * 
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 动态条件查询菜品
     * 
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     * 
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}
