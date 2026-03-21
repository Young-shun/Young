package com.sky.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController("userOrderController")
@Slf4j
@RequestMapping("/user/order")
public class OrderController {
  @Autowired
  private OrderService orderService;

  /**
   * 提交订单
   * 
   * @param ordersSubmitDTO
   * @return
   */
  @PostMapping("/submit")
  public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
    log.info("Submitting order: {}", ordersSubmitDTO);
    OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
    return Result.success(orderSubmitVO);
  }

  /**
   * 订单支付
   *
   * @param ordersPaymentDTO
   * @return
   */
  @PutMapping("/payment")
  @ApiOperation("订单支付")
  public Result<String> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
    log.info("订单支付：{}", ordersPaymentDTO);
    orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
    log.info("生成预支付交易单：{}", ordersPaymentDTO.getOrderNumber());
    return Result.success();
  }

  /**
   * 获取用户历史订单
   *
   * @param
   * @return
   */
  @GetMapping("/historyOrders")
  public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
    log.info("获取用户历史订单：{}", ordersPageQueryDTO);
    PageResult pageResult = orderService.getHistoryOrders(ordersPageQueryDTO);
    return Result.success(pageResult);
  }

  /**
   * 获取用户订单详情
   *
   * @param orderId
   * @return
   */
  @GetMapping("/orderDetail/{id}")
  public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
    log.info("获取用户订单详情：{}", id);
    OrderVO orderVO = orderService.getOrderDetail(id);
    return Result.success(orderVO);
  }

  /**
   * 用户取消订单
   *
   * @return
   */
  @PutMapping("/cancel/{id}")
  @ApiOperation("取消订单")
  public Result cancel(@PathVariable("id") Long id) throws Exception {
    orderService.userCancelById(id);
    return Result.success();
  }

  /**
   * 再来一单
   *
   * @param id
   * @return
   */
  @PostMapping("/repetition/{id}")
  @ApiOperation("再来一单")
  public Result repetition(@PathVariable Long id) {
    orderService.repetition(id);
    return Result.success();
  }

  /**
   * 订单提醒
   * 
   * @param id
   * @return
   */
  @GetMapping("/reminder/{id}")
  public Result<String> reminder(@PathVariable Long id) {
    orderService.reminder(id);
    return Result.success();
  }

}
