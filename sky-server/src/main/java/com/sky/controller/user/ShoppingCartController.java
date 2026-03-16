package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/user/shoppingCart")
@Slf4j
@RestController
public class ShoppingCartController {

  @Autowired
  private ShoppingCartService shoppingCartService;

  /**
   * 添加购物车商品
   * 
   * @param shoppingCartDTO
   * @return
   */
  @PostMapping("/add")
  public Result<String> addItemToCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    log.info("Adding item to cart: {}", shoppingCartDTO);
    shoppingCartService.addItem(shoppingCartDTO);
    return Result.success();
  }

  /**
   * 查看购物车
   * 
   * @return
   */
  @GetMapping("/list")
  public Result<List<ShoppingCart>> listCartItems() {
    log.info("Listing cart items");
    List<ShoppingCart> cartItems = shoppingCartService.listItems();
    return Result.success(cartItems);
  }

  /**
   * 清空购物车
   * 
   * @return
   */
  @DeleteMapping("/clean")
  public Result<String> cleanCart() {
    log.info("Cleaning cart");
    shoppingCartService.cleanCart();
    return Result.success();
  }

  /**
   * 删除购物车商品中的一个
   * 
   * @param shoppingCartDTO
   * @return
   */
  @PostMapping("/sub")
  public Result<String> cleanSub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    log.info("Removing item from cart: {}", shoppingCartDTO);
    shoppingCartService.deleteSubItem(shoppingCartDTO);
    return Result.success();
  }

}
