package com.sky.api.client;

import com.sky.entity.ShoppingCart;
import com.sky.api.client.fallback.ShoppingCartClientFallbackFactory;
import com.sky.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "sky-take-out-shoppingcart", fallbackFactory = ShoppingCartClientFallbackFactory.class)
public interface ShoppingCartClient {

  // controller in shoppingcart-server is under
  // @RequestMapping("/user/shoppingCart")
  @PostMapping("/user/shoppingCart/add")
  Result<String> addItem(@RequestBody ShoppingCart shoppingCart);

  @GetMapping("/user/shoppingCart/list")
  Result<List<ShoppingCart>> listItems();

  @DeleteMapping("/user/shoppingCart/clean")
  Result<String> cleanCart();

}
