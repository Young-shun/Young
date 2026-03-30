package com.sky.controller.user;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 商店管理控制器（user）
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {
  public static String Key = "shop_status";
  @Autowired
  @Resource(name = "redisTemplate")
  private RedisTemplate<String, Integer> redisTemplate;

  @GetMapping("/status")
  /**
   * 作用: 执行getStatus相关逻辑。
   * 输入: 无。
   * 输出: Result<Integer>。
   */
  public Result<Integer> getStatus() {
    log.info("获取商店状态");
    Integer status = (Integer) redisTemplate.opsForValue().get(Key);
    return Result.success(status);
  }
}
