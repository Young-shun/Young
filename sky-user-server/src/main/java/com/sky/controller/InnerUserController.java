package com.sky.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;

@RestController
@RequestMapping("/inner/user")
public class InnerUserController {

  @Autowired
  private UserMapper userMapper;

  @GetMapping("/newCount")
  public Result<Long> newCount(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
        .ge(User::getCreateTime, begin)
        .lt(User::getCreateTime, end));
    return Result.success(count);
  }

  @GetMapping("/totalCount")
  public Result<Long> totalCount(
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
        .lt(User::getCreateTime, end));
    return Result.success(count);
  }
}
