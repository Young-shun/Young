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
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
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

  @Transactional
  /**
   * 作用: 执行saveWithFlavor相关逻辑。
   * 输入: DishDTO dishDTO。
   * 输出: 无。
   */
  public void saveWithFlavor(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    dishMapper.insert(dish);
    Long dishId = dish.getId();
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && !flavors.isEmpty()) {
      flavors.forEach(flavor -> {
        flavor.setDishId(dishId);
        dishFlavorMapper.insert(flavor);
      });
    }
  }

  @Override
  /**
   * 作用: 执行delete相关逻辑。
   * 输入: List<Long> ids。
   * 输出: 无。
   */
  public void delete(List<Long> ids) {
    for (Long id : ids) {
      Dish dish = dishMapper.selectById(id);
      if (dish != null && dish.getStatus() == StatusConstant.ENABLE) {
        throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
      }
    }
    dishMapper.deleteBatchIds(ids);
    dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().in(DishFlavor::getDishId, ids));
  }

  @Override
  /**
   * 作用: 执行getByIdWithFlavor相关逻辑。
   * 输入: Long id。
   * 输出: DishVO。
   */
  public DishVO getByIdWithFlavor(Long id) {
    Dish dish = dishMapper.selectById(id);
    if (dish == null) {
      return null;
    }
    List<DishFlavor> flavors = dishFlavorMapper
        .selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
    DishVO dishVO = new DishVO();
    BeanUtils.copyProperties(dish, dishVO);
    dishVO.setFlavors(flavors);
    return dishVO;
  }

  @Override
  /**
   * 作用: 执行updateWithFlavor相关逻辑。
   * 输入: DishDTO dishDTO。
   * 输出: 无。
   */
  public void updateWithFlavor(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    dishMapper.updateById(dish);
    dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if (flavors != null && !flavors.isEmpty()) {
      flavors.forEach(flavor -> {
        flavor.setDishId(dish.getId());
        dishFlavorMapper.insert(flavor);
      });
    }
  }

  /**
   * 作用: 执行startOrStop相关逻辑。
   * 输入: Integer status, Long id。
   * 输出: 无。
   */
  public void startOrStop(Integer status, Long id) {
    Dish dish = Dish.builder()
        .id(id)
        .status(status)
        .build();
    dishMapper.updateById(dish);
  }

  /**
   * 作用: 执行list相关逻辑。
   * 输入: Long categoryId。
   * 输出: List<Dish>。
   */
  public List<Dish> list(Long categoryId) {
    Dish dish = Dish.builder()
        .categoryId(categoryId)
        .status(StatusConstant.ENABLE)
        .build();
    return dishMapper.list(dish);
  }

  /**
   * 作用: 执行listWithFlavor相关逻辑。
   * 输入: Dish dish。
   * 输出: List<DishVO>。
   */
  public List<DishVO> listWithFlavor(Dish dish) {
    List<Dish> dishList = dishMapper.list(dish);
    List<DishVO> dishVOList = new ArrayList<>();
    for (Dish d : dishList) {
      DishVO dishVO = new DishVO();
      BeanUtils.copyProperties(d, dishVO);
      List<DishFlavor> flavors = dishFlavorMapper
          .selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, d.getId()));
      dishVO.setFlavors(flavors);
      dishVOList.add(dishVO);
    }
    return dishVOList;
  }

  @Override
  /**
   * 作用: 执行getByIdEntity相关逻辑。
   * 输入: Long id。
   * 输出: Dish。
   */
  public Dish getByIdEntity(Long id) {
    return dishMapper.selectById(id);
  }

  @Override
  /**
   * 作用: 执行queryDishByPage相关逻辑。
   * 输入: DishPageQueryDTO dishPageQueryDTO。
   * 输出: Page<DishVO>。
   */
  public Page<DishVO> queryDishByPage(DishPageQueryDTO dishPageQueryDTO) {
    Page<DishVO> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
    dishMapper.page(page, dishPageQueryDTO);
    return page;
  }
}
