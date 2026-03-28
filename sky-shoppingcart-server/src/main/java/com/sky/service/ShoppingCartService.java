package com.sky.service;

import java.util.List;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCartService {

  void addItem(ShoppingCartDTO shoppingCartDTO);

  List<ShoppingCart> listItems();

  void cleanCart();

  void deleteSubItem(ShoppingCartDTO shoppingCartDTO);

}
