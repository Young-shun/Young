package com.sky.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

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

  /**
   * 根据订单id查询订单详情
   * 
   * @param id
   * @return
   */
  @Select("select * from orders where id = #{id}")
  OrderVO getOrderDetail(Long id);

  /**
   * 根据状态统计订单数量
   * 
   * @param status
   */
  @Select("select count(id) from orders where status = #{status}")
  Integer countStatus(Integer status);

  @Select("select * from orders where status = #{pendingPayment} and order_time < #{timeoutTime}")
  List<Orders> updateOrderStatusToTimeout(Integer pendingPayment, LocalDateTime timeoutTime);

  @Select("select number from orders where id = #{id}")
  String getNumberById(Long id);

  /**
   * 统计当日销售额
   * 
   * @param beginTime
   * @param endTime
   * @param status
   * @return
   */
  @Select("select sum(amount) from orders where status = #{status} and order_time >= #{beginTime} and order_time < #{endTime} ")
  Double getTotalAmount(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

  /**
   * 统计订单数量
   * 
   * @param beginTime
   * @param endTime
   * @return
   */
  @Select("select count(*) from orders where order_time >= #{beginTime} and order_time < #{endTime}")
  Integer getTotalOrderCount(LocalDateTime beginTime, LocalDateTime endTime);

  /**
   * 统计有效订单数量
   * 
   * @param beginTime
   * @param endTime
   * @param completed
   * @return
   */
  @Select("select count(*) from orders where order_time >= #{beginTime} and order_time < #{endTime} and status = #{completed}")
  Integer getValidOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer completed);

  /**
   * 查询商品销量排名
   * 
   * @param begin
   * @param end
   */
  List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);

}
