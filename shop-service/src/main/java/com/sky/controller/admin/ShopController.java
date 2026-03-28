package com.sky.controller.admin;

import javax.annotation.Resource;

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

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
  public static String Key = "shop_status";
  @Autowired
  @Resource(name = "redisTemplate")
  private RedisTemplate redisTemplate;

  @PutMapping("/{status}")
  public Result<String> setStatus(@PathVariable Integer status) {
    log.info("set shop status: {}", status);
    redisTemplate.opsForValue().set("shop_status", status);
    return Result.success();
  }

  @GetMapping("/status")
  public Result<Integer> getStatus() {
    log.info("get shop status");
    Integer status = (Integer) redisTemplate.opsForValue().get(Key);
    return Result.success(status);
  }
}
