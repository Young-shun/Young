package com.sky.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.api.client.UserClient;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户管理控制器
 */
@RestController("userController")
@RequestMapping("/user/user")
@Slf4j
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private JwtProperties jwtProperties;

  /**
   * 微信登入
   * 
   * @param entity
   * @return
   */
  @PostMapping("/login")
  public Result<UserLoginVO> login(@RequestBody UserLoginDTO entity) {
    // 登录逻辑
    log.info("微信登录：{}", entity.getCode());

    User user = userService.login(entity);

    // 登录成功后，生成jwt令牌
    Map<String, Object> claims = new HashMap<>();
    claims.put(JwtClaimsConstant.USER_ID, user.getId());
    String token = JwtUtil.createJWT(
        jwtProperties.getUserSecretKey(),
        jwtProperties.getUserTtl(),
        claims);
    UserLoginVO userLoginVO = UserLoginVO.builder()
        .id(user.getId())
        .openid(user.getOpenid())
        .token(token)
        .build();
    return Result.success(userLoginVO);
  }

  @GetMapping("/user/newCount")
  /**
   * 作用: 执行newCount相关逻辑。
   * 输入: LocalDateTime begin, LocalDateTime end。
   * 输出: Result<Long>。
   */
  public Result<Long> newCount(LocalDateTime begin, LocalDateTime end) {
    Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
        .ge(User::getCreateTime, begin)
        .lt(User::getCreateTime, end));
    return Result.success(count);
  }

  @GetMapping("/user/totalCount")
  /**
   * 作用: 执行totalCount相关逻辑。
   * 输入: LocalDateTime end。
   * 输出: Result<Long>。
   */
  public Result<Long> totalCount(LocalDateTime end) {
    Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
        .lt(User::getCreateTime, end));
    return Result.success(count);
  }
}
