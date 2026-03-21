package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.ShoppingCart;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
  /**
   * 查询购物车
   */
  List<ShoppingCart> list(ShoppingCart shoppingCart);

}
