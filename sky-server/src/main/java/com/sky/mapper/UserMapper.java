package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.User;

@Mapper
public interface UserMapper {
  /**
   * 根据openid查询用户信息
   * 
   * @param openid
   * @return
   */
  @Select("select * from user where openid = #{openid}")
  User getByOpenid(String openid);

  void insert(User user);

}
