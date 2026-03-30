package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService extends IService<Orders> {

  /**
   * 作用: 执行submitOrder相关逻辑。
   * 输入: OrdersSubmitDTO ordersSubmitDTO。
   * 输出: OrderSubmitVO。
   */
  OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

  /**
   * 作用: 执行paySuccess相关逻辑。
   * 输入: String outTradeNo。
   * 输出: 无。
   */
  void paySuccess(String outTradeNo);

  /**
   * 作用: 执行getHistoryOrders相关逻辑。
   * 输入: OrdersPageQueryDTO ordersPageQueryDTO。
   * 输出: PageResult。
   */
  PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO);

  /**
   * 作用: 执行getOrderDetail相关逻辑。
   * 输入: Long id。
   * 输出: OrderVO。
   */
  OrderVO getOrderDetail(Long id);

  /**
   * 作用: 执行userCancelById相关逻辑。
   * 输入: Long id。
   * 输出: 无。
   */
  void userCancelById(Long id);

  /**
   * 作用: 执行repetition相关逻辑。
   * 输入: Long id。
   * 输出: 无。
   */
  void repetition(Long id);

  /**
   * 作用: 执行statistics相关逻辑。
   * 输入: 无。
   * 输出: OrderStatisticsVO。
   */
  OrderStatisticsVO statistics();

  /**
   * 作用: 执行confirm相关逻辑。
   * 输入: OrdersConfirmDTO ordersConfirmDTO。
   * 输出: 无。
   */
  void confirm(OrdersConfirmDTO ordersConfirmDTO);

  /**
   * 作用: 执行rejection相关逻辑。
   * 输入: OrdersRejectionDTO ordersRejectionDTO。
   * 输出: 无。
   */
  void rejection(OrdersRejectionDTO ordersRejectionDTO);

  /**
   * 作用: 执行delivery相关逻辑。
   * 输入: Long id。
   * 输出: 无。
   */
  void delivery(Long id);

  /**
   * 作用: 执行complete相关逻辑。
   * 输入: Long id。
   * 输出: 无。
   */
  void complete(Long id);

  /**
   * 作用: 执行reminder相关逻辑。
   * 输入: Long id。
   * 输出: 无。
   */
  void reminder(Long id);

  /**
   * 作用: 执行queryOrderByPage相关逻辑。
   * 输入: OrdersPageQueryDTO ordersPageQueryDTO。
   * 输出: PageResult。
   */
  PageResult queryOrderByPage(OrdersPageQueryDTO ordersPageQueryDTO);

}
