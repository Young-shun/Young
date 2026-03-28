package com.sky.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
  /**
   * 根据openid查询用户信息
   * 
   * @param openid
   * @return
   */
  @Select("select * from user where openid = #{openid}")
  User getByOpenid(String openid);

}
