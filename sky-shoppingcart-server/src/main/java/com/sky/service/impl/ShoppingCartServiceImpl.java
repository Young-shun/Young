package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private com.sky.api.client.SetmealClient setmealClient;
  @Autowired
  private com.sky.api.client.DishClient dishClient;

  /**
   * 添加购物车商品
   */
  @Override
  public void addItem(ShoppingCartDTO shoppingCartDTO) {
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
    // 获取当前用户id
    Long userId = BaseContext.getCurrentId();
    if (userId == null) {
      throw new BaseException("用户未登录或用户上下文丢失");
    }
    shoppingCart.setUserId(userId);
    // 判断是否存在
    List<ShoppingCart> cartItems = shoppingCartMapper.list(shoppingCart);

    if (cartItems != null && !cartItems.isEmpty()) {
      // 购物车已存在，更新数量
      ShoppingCart existingCartItem = cartItems.get(0);
      existingCartItem.setNumber(existingCartItem.getNumber() + 1);
      shoppingCartMapper.updateById(existingCartItem);
    } else {
      // 判断是否是套餐
      if (shoppingCart.getSetmealId() != null) {
        // 添加到购物车的是套餐，通过 RPC 获取
        Setmeal setmeal;
        try {
          setmeal = setmealClient.getById(shoppingCartDTO.getSetmealId());
        } catch (Exception ex) {
          log.error("调用套餐服务失败, setmealId={}", shoppingCartDTO.getSetmealId(), ex);
          throw new BaseException("调用套餐服务失败: " + ex.getMessage());
        }
        if (setmeal == null || setmeal.getPrice() == null) {
          throw new BaseException("套餐不存在或价格异常");
        }
        shoppingCart.setName(setmeal.getName());
        shoppingCart.setImage(setmeal.getImage());
        shoppingCart.setAmount(setmeal.getPrice());
      } else {
        // 不是套餐，插入单品，通过 RPC 获取
        if (shoppingCartDTO.getDishId() == null) {
          throw new BaseException("菜品ID不能为空");
        }
        Dish dish;
        try {
          dish = dishClient.getById(shoppingCartDTO.getDishId());
        } catch (Exception ex) {
          log.error("调用菜品服务失败, dishId={}", shoppingCartDTO.getDishId(), ex);
          throw new BaseException("调用菜品服务失败: " + ex.getMessage());
        }
        if (dish == null || dish.getPrice() == null) {
          throw new BaseException("菜品不存在或价格异常");
        }
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
    if (userId == null) {
      throw new BaseException("用户未登录或用户上下文丢失");
    }
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
    if (userId == null) {
      throw new BaseException("用户未登录或用户上下文丢失");
    }
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("user_id", userId);
    shoppingCartMapper.deleteByMap(columnMap);
  }

  /**
   * 删除购物车商品
   */
  @Override
  public void deleteSubItem(ShoppingCartDTO shoppingCartDTO) {
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
    Long userId = BaseContext.getCurrentId();
    if (userId == null) {
      throw new BaseException("用户未登录或用户上下文丢失");
    }
    shoppingCart.setUserId(userId);
    List<ShoppingCart> items = shoppingCartMapper.list(shoppingCart);
    if (items == null || items.isEmpty()) {
      return;
    }
    shoppingCart = items.get(0);
    shoppingCart.setNumber(shoppingCart.getNumber() - 1);
    if (shoppingCart.getNumber() > 0) {
      shoppingCartMapper.updateById(shoppingCart);
    } else {
      QueryWrapper<ShoppingCart> wrapper = new QueryWrapper<>();
      wrapper.eq(userId != null, "user_id", userId)
          .eq(shoppingCart.getDishId() != null, "dish_id", shoppingCart.getDishId())
          .eq(shoppingCart.getSetmealId() != null, "setmeal_id", shoppingCart.getSetmealId())
          .eq(shoppingCart.getDishFlavor() != null, "dish_flavor", shoppingCart.getDishFlavor());
      shoppingCartMapper.delete(wrapper);
    }
  }

}
