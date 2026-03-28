package com.sky.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.api.client.DishClient;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.setmealDishMapper;
import com.sky.service.SetMealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {

  @Autowired
  private SetmealMapper setmealMapper;
  @Autowired
  private setmealDishMapper setmealDishMapper;
  @Autowired
  private DishClient dishClient;

  @Override
  @Transactional
  public void createSetMeal(SetmealDTO setMealDto) {
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setMealDto, setmeal);
    setmeal.setStatus(StatusConstant.DISABLE);
    setmealMapper.insert(setmeal);
    Long setmealId = setmeal.getId();
    List<SetmealDish> dishs = setMealDto.getSetmealDishes();
    if (dishs != null && !dishs.isEmpty()) {
      dishs.forEach(dish -> {
        dish.setSetmealId(setmealId);
        setmealDishMapper.insert(dish);
      });
    }
  }

  @Override
  public void delete(List<Long> ids) {
    for (Long id : ids) {
      Setmeal setmeal = setmealMapper.selectById(id);
      if (setmeal != null && setmeal.getStatus() == StatusConstant.ENABLE) {
        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
      }
    }
    setmealMapper.deleteBatchIds(ids);
    setmealDishMapper.deleteBatchIds(ids);
  }

  @Override
  public SetmealVO getByIdWithDish(Long id) {
    Setmeal setmeal = setmealMapper.selectById(id);
    if (setmeal == null) {
      return null;
    }
    List<SetmealDish> dishes = setmealDishMapper
        .selectList(new QueryWrapper<SetmealDish>().lambda().eq(SetmealDish::getSetmealId, id));
    SetmealVO setmealVO = new SetmealVO();
    BeanUtils.copyProperties(setmeal, setmealVO);
    setmealVO.setSetmealDishes(dishes);
    return setmealVO;
  }

  @Override
  public void updateWithDish(SetmealDTO setmealDTO) {
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDTO, setmeal);
    setmealMapper.updateById(setmeal);
    setmealDishMapper.deleteBatchIds(List.of(setmeal.getId()));
    List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
    if (dishes != null && !dishes.isEmpty()) {
      dishes.forEach(dish -> {
        dish.setSetmealId(setmeal.getId());
        setmealDishMapper.insert(dish);
      });
    }
  }

  public void startOrStop(Integer status, Long id) {
    Setmeal setmeal = Setmeal.builder()
        .id(id)
        .status(status)
        .build();
    setmealMapper.updateById(setmeal);
  }

  public List<Setmeal> list(Setmeal setmeal) {
    QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
    queryWrapper.lambda()
        .eq(setmeal.getId() != null, Setmeal::getId, setmeal.getId())
        .like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName())
        .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
    List<Setmeal> list = setmealMapper.selectList(queryWrapper);
    return list;
  }

  @Override
  public Setmeal getByIdEntity(Long id) {
    return setmealMapper.selectById(id);
  }

  public List<DishItemVO> getDishItemById(Long id) {
    return setmealMapper.getDishItemBySetmealId(id);
  }

  @Override
  public Page<SetmealVO> querySetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
    Page<SetmealVO> page = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
    setmealMapper.pageQuery(page, setmealPageQueryDTO);
    return page;
  }

}
