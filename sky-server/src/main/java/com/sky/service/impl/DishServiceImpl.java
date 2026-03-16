package com.sky.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;

import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
  @Autowired
  private DishMapper dishMapper;
  @Autowired
  private DishFlavorMapper dishFlavorMapper;

  /**
   * 新增菜品
   * 
   * @param dishDTO
   */
  @Transactional
  public void saveWithFlavor(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    // 向菜品表插入数据
    dishMapper.insert(dish);
    Long dishId = dish.getId();
    // 向口味表插入数据
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && !flavors.isEmpty()) {
      flavors.forEach(flavor -> {
        flavor.setDishId(dishId);
        dishFlavorMapper.insert(flavor);
      });
    }
  }

  /**
   * 批量删除菜品
   * 
   * @param ids
   */
  @Override
  public void delete(List<Long> ids) {
    // 判断是否能够删除，状态为1不可删除
    for (Long id : ids) {
      Dish dish = dishMapper.selectById(id);
      if (dish != null && dish.getStatus() == StatusConstant.ENABLE) {
        throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
      }
    }
    // 判断是否能够删除，是否被套餐关联
    for (Long id : ids) {
      Long count = Db.lambdaQuery(SetmealDish.class)
          .eq(SetmealDish::getDishId, id).count();
      if (count > 0) {
        // 当前菜品有在售套餐，不能删除
        throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
      }
    }
    // 删除菜品数据
    // for (Long id : ids) {
    // dishMapper.deleteById(id);
    // dishFlavorMapper.deleteByDishId(id);
    // }
    dishMapper.deleteBatchIds(ids);
    dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().in(DishFlavor::getDishId, ids));

    // 删除关联的口味表数据

  }

  /**
   * 根据id查询菜品和口味
   * 
   * @param id
   * @return
   */
  @Override
  public DishVO getByIdWithFlavor(Long id) {
    // 查菜品数据
    Dish dish = dishMapper.selectById(id);
    if (dish == null) {
      return null;
    }
    // 查口味数据
    List<DishFlavor> flavors = dishFlavorMapper
        .selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
    DishVO dishVO = new DishVO();
    BeanUtils.copyProperties(dish, dishVO);
    dishVO.setFlavors(flavors);
    return dishVO;
  }

  /**
   * 更新菜品
   */
  @Override
  public void updateWithFlavor(DishDTO dishDTO) {
    // 修改菜品表基本信息
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    dishMapper.updateById(dish);
    // 删除原有的口味信息
    dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));
    // 插入新的口味信息
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && !flavors.isEmpty()) {
      flavors.forEach(flavor -> {
        flavor.setDishId(dish.getId());
        dishFlavorMapper.insert(flavor);
      });
    }
  }

  /**
   * 启用、禁用分类
   * 
   * @param status
   * @param id
   */
  public void startOrStop(Integer status, Long id) {
    Dish dish = Dish.builder()
        .id(id)
        .status(status)
        // .updateTime(LocalDateTime.now())
        // .updateUser(BaseContext.getCurrentId())
        .build();
    dishMapper.updateById(dish);
  }

  /**
   * 根据分类id查询菜品
   * 
   * @param categoryId
   * @return
   */
  public List<Dish> list(Long categoryId) {
    Dish dish = Dish.builder()
        .categoryId(categoryId)
        .status(StatusConstant.ENABLE)
        .build();
    return dishMapper.list(dish);
  }

  /**
   * 条件查询菜品和口味
   * 
   * @param dish
   * @return
   */
  public List<DishVO> listWithFlavor(Dish dish) {
    List<Dish> dishList = dishMapper.list(dish);

    List<DishVO> dishVOList = new ArrayList<>();

    for (Dish d : dishList) {
      DishVO dishVO = new DishVO();
      BeanUtils.copyProperties(d, dishVO);

      // 根据菜品id查询对应的口味
      List<DishFlavor> flavors = dishFlavorMapper
          .selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, d.getId()));

      dishVO.setFlavors(flavors);
      dishVOList.add(dishVO);
    }

    return dishVOList;
  }

  /**
   * 分页查询菜品
   */
  @Override
  public Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO) {
    // 1.构建条件
    Page<DishVO> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
    dishMapper.page(page, dishPageQueryDTO);
    // 3.封装返回
    return page;
  }
}
