package com.sky.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商店管理控制器
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {
  public static String Key = "shop_status";
  @Autowired
  private RedisTemplate redisTemplate;

  /**
   * 获取商店状态
   * 
   * @return
   */
  @GetMapping("/status")
  public Result<Integer> getStatus() {
    log.info("获取商店状态");
    Integer status = (Integer) redisTemplate.opsForValue().get(Key);
    return Result.success(status);
  }
}
