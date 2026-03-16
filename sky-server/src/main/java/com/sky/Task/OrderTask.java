package com.sky.Task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderTask {

  @Autowired
  private OrderMapper orderMapper;

  /**
   * 处理支付超时订单
   */
  @Scheduled(cron = "0 0/1 * * * ?")
  public void processTimeoutOrders() {
    log.info("处理支付超时订单");
    LocalDateTime timeoutTime = LocalDateTime.now().plusMinutes(-15);
    List<Orders> timeoutOrders = orderMapper.selectList(new LambdaQueryWrapper<Orders>()
        .eq(Orders::getStatus, Orders.PENDING_PAYMENT)
        .lt(Orders::getDeliveryTime, timeoutTime));
    // 处理超时订单
    if (timeoutOrders != null && !timeoutOrders.isEmpty()) {
      timeoutOrders.forEach(order -> {
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("支付超时");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("超时订单: {}", order);
      });
    }
  }

  /**
   * 处理已发货订单
   */
  @Scheduled(cron = "0 0 1 * * ?")
  public void processDELIVERYOrders() {
    log.info("处理已发货订单");
    LocalDateTime timeoutTime = LocalDateTime.now().plusMinutes(-60);
    List<Orders> timeoutOrders = orderMapper.selectList(new LambdaQueryWrapper<Orders>()
        .eq(Orders::getStatus, Orders.DELIVERY_IN_PROGRESS)
        .lt(Orders::getDeliveryTime, timeoutTime));
    // 处理超时订单
    if (timeoutOrders != null && !timeoutOrders.isEmpty()) {
      timeoutOrders.forEach(order -> {
        order.setStatus(Orders.COMPLETED);
        orderMapper.updateById(order);
        log.info("完成订单: {}", order);
      });
    }

  }

}
