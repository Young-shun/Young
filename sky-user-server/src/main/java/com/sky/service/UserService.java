package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService extends IService<User> {
  /**
   * 作用: 执行login相关逻辑。
   * 输入: UserLoginDTO userLoginDTO。
   * 输出: User。
   */
  User login(UserLoginDTO userLoginDTO);
}
