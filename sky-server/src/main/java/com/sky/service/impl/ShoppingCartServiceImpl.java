package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private SetmealMapper setmealMapper;
  @Autowired
  private DishMapper dishMapper;

  /**
   * 添加购物车商品
   */
  @Override
  public void addItem(ShoppingCartDTO shoppingCartDTO) {
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
    // 获取当前用户id
    Long userId = BaseContext.getCurrentId();
    shoppingCart.setUserId(userId);
    // 判断是否存在
    List<ShoppingCart> cartItems = shoppingCartMapper.list(shoppingCart);

    if (cartItems != null && !cartItems.isEmpty()) {
      // 购物车已存在，更新数量
      ShoppingCart existingCartItem = cartItems.get(0);
      existingCartItem.setNumber(existingCartItem.getNumber() + 1);
      shoppingCartMapper.updateNumberById(existingCartItem);
    } else {
      // 判断是否是套餐
      if (shoppingCart.getSetmealId() != null) {
        // 添加到购物车的是套餐
        Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
        shoppingCart.setName(setmeal.getName());
        shoppingCart.setImage(setmeal.getImage());
        shoppingCart.setAmount(setmeal.getPrice());
      } else {
        // 不是套餐，插入单品
        Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
        shoppingCart.setName(dish.getName());
        shoppingCart.setImage(dish.getImage());
        shoppingCart.setAmount(dish.getPrice());
      }
      shoppingCart.setNumber(1);
      shoppingCart.setCreateTime(LocalDateTime.now());
      shoppingCartMapper.insert(shoppingCart);
    }
  }

  /**
   * 查看购物车
   */
  @Override
  public List<ShoppingCart> listItems() {
    Long userId = BaseContext.getCurrentId();
    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUserId(userId);
    return shoppingCartMapper.list(shoppingCart);
  }

  /**
   * 清空购物车
   */
  @Override
  public void cleanCart() {
    Long userId = BaseContext.getCurrentId();
    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUserId(userId);
    shoppingCartMapper.clean(shoppingCart);
  }

  @Override
  public void deleteSubItem(ShoppingCartDTO shoppingCartDTO) {
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
    Long userId = BaseContext.getCurrentId();
    shoppingCart.setUserId(userId);
    shoppingCart = shoppingCartMapper.list(shoppingCart).get(0);
    shoppingCart.setNumber(shoppingCart.getNumber() - 1);
    if (shoppingCart.getNumber() > 0) {
      shoppingCartMapper.updateNumberById(shoppingCart);
    } else {
      shoppingCartMapper.deleteSubItem(shoppingCart);
    }
  }

}
