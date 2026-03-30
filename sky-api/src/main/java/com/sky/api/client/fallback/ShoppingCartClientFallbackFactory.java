package com.sky.api.client.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.ShoppingCartClient;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ShoppingCartClientFallbackFactory implements FallbackFactory<ShoppingCartClient> {

  @Override
  public ShoppingCartClient create(Throwable cause) {
    return new ShoppingCartClient() {
      @Override
      public Result<String> addItem(ShoppingCart shoppingCart) {
        log.error("Feign调用ShoppingCartClient#addItem失败, shoppingCart={}", shoppingCart, cause);
        return Result.error("购物车服务暂时不可用");
      }

      @Override
      public Result<List<ShoppingCart>> listItems() {
        log.error("Feign调用ShoppingCartClient#listItems失败", cause);
        return Result.success(Collections.emptyList());
      }

      @Override
      public Result<String> cleanCart() {
        log.error("Feign调用ShoppingCartClient#cleanCart失败", cause);
        return Result.error("购物车服务暂时不可用");
      }
    };
  }
}