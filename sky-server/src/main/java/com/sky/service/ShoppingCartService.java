package com.sky.service;

import java.util.List;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCartService {

  /**
   * 作用: 执行addItem相关逻辑。
   * 输入: ShoppingCartDTO shoppingCartDTO。
   * 输出: 无。
   */
  void addItem(ShoppingCartDTO shoppingCartDTO);

  List<ShoppingCart> listItems();

  /**
   * 作用: 执行cleanCart相关逻辑。
   * 输入: 无。
   * 输出: 无。
   */
  void cleanCart();

  /**
   * 作用: 执行deleteSubItem相关逻辑。
   * 输入: ShoppingCartDTO shoppingCartDTO。
   * 输出: 无。
   */
  void deleteSubItem(ShoppingCartDTO shoppingCartDTO);

}
