package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.entity.Orders;

@Mapper
public interface OrderMapper {

  void insert(Orders order);

  /**
   * 根据订单号和用户id查询订单
   * 
   * @param orderNumber
   * @param userId
   */
  @Select("select * from orders where number = #{orderNumber} and user_id= #{userId}")
  Orders getByNumberAndUserId(String orderNumber, Long userId);

  /**
   * 修改订单信息
   * 
   * @param orders
   */
  void update(Orders orders);
}
