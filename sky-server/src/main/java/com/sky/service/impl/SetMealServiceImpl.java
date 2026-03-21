package com.sky.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.setmealDishMapper;
import com.sky.result.PageResult;
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
  private DishMapper dishMapper;

  /**
   * 新增套餐，同时需要保存套餐和菜品的关联关系
   * 
   * @param setMealDto
   */
  @Override
  @Transactional
  public void createSetMeal(SetmealDTO setMealDto) {
    Setmeal setmeal = new Setmeal();
    // 对象属性拷贝
    BeanUtils.copyProperties(setMealDto, setmeal);
    // 设置默认值
    setmeal.setStatus(StatusConstant.DISABLE);
    // 插入数据库
    // 向套餐表插入数据
    setmealMapper.insert(setmeal);
    Long setmealId = setmeal.getId();
    // 向口味表插入数据
    List<SetmealDish> dishs = setMealDto.getSetmealDishes();
    if (dishs != null && !dishs.isEmpty()) {
      // 给每一个dish设置套餐id，同时插入表中
      dishs.forEach(dish -> {
        dish.setSetmealId(setmealId);
        setmealDishMapper.insert(dish);
      });
    }
  }

  /**
   * 批量删除
   * 
   * @param ids
   */
  @Override
  public void delete(List<Long> ids) {
    // 判断是否能够删除，状态为1不可删除
    for (Long id : ids) {
      Setmeal setmeal = setmealMapper.selectById(id);
      if (setmeal != null && setmeal.getStatus() == StatusConstant.ENABLE) {
        throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
      }
    }
    // 删除菜品数据
    // for (Long id : ids) {
    // dishMapper.deleteById(id);
    // dishFlavorMapper.deleteByDishId(id);
    // }
    setmealMapper.deleteBatchIds(ids);
    setmealDishMapper.deleteBatchIds(ids);
  }

  /**
   * 根据id查询套餐和菜品
   * 
   * @param id
   * @return
   */
  @Override
  public SetmealVO getByIdWithDish(Long id) {
    // 查套餐数据
    Setmeal setmeal = setmealMapper.selectById(id);
    if (setmeal == null) {
      return null;
    }
    // 查菜品数据
    List<SetmealDish> dishes = setmealDishMapper
        .selectList(new QueryWrapper<SetmealDish>().lambda().eq(SetmealDish::getSetmealId, id));
    SetmealVO setmealVO = new SetmealVO();
    BeanUtils.copyProperties(setmeal, setmealVO);
    setmealVO.setSetmealDishes(dishes);
    return setmealVO;
  }

  /**
   * 更新菜品
   */
  @Override
  public void updateWithDish(SetmealDTO setmealDTO) {
    // 修改套餐表基本信息
    Setmeal setmeal = new Setmeal();
    BeanUtils.copyProperties(setmealDTO, setmeal);
    setmealMapper.updateById(setmeal);
    // 删除原有的菜品信息
    setmealDishMapper.deleteBatchIds(List.of(setmeal.getId()));
    // 插入新的菜品信息
    List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
    if (dishes != null && !dishes.isEmpty()) {
      dishes.forEach(dish -> {
        dish.setSetmealId(setmeal.getId());
        setmealDishMapper.insert(dish);
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
    Setmeal setmeal = Setmeal.builder()
        .id(id)
        .status(status)
        // .updateTime(LocalDateTime.now())
        // .updateUser(BaseContext.getCurrentId())
        .build();
    setmealMapper.updateById(setmeal);
  }

  /**
   * 条件查询
   * 
   * @param setmeal
   * @return
   */
  public List<Setmeal> list(Setmeal setmeal) {
    QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
    queryWrapper.lambda()
        .eq(setmeal.getId() != null, Setmeal::getId, setmeal.getId())
        .like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName())
        .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
    List<Setmeal> list = setmealMapper.selectList(queryWrapper);
    return list;
  }

  /**
   * 根据id查询菜品选项
   * 
   * @param id
   * @return
   */
  public List<DishItemVO> getDishItemById(Long id) {
    return setmealMapper.getDishItemBySetmealId(id);
  }

}
