package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.sky.entity.ShoppingCart;

@Mapper
public interface ShoppingCartMapper {
  /**
   * 查询购物车
   */
  List<ShoppingCart> list(ShoppingCart shoppingCart);

  /**
   * 添加购物车
   */
  @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) "
      +
      "values (#{name}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image}, #{createTime})")
  void insert(ShoppingCart shoppingCart);

  /**
   * 更新商品数量
   *
   * @param shoppingCart
   */
  @Update("update shopping_cart set number = #{number} where id = #{id}")
  void updateNumberById(ShoppingCart shoppingCart);

  /**
   * 清空购物车
   * 
   * @param shoppingCart
   */
  @Delete("delete from shopping_cart where user_id = #{userId}")
  void clean(ShoppingCart shoppingCart);

  void deleteSubItem(ShoppingCart shoppingCart);

}
