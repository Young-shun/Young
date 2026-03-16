package com.sky.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private OrderDetailMapper orderDetailMapper;
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private AddressBookMapper addressBookMapper;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private WeChatPayUtil weChatPayUtil;

  @Override
  @Transactional
  public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
    // 先判断地址簿和购物车是否为空
    AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
    if (addressBook == null) {
      throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
    }
    Long userId = BaseContext.getCurrentId();

    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUserId(userId);
    List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
    if (list == null || list.isEmpty()) {
      throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
    }

    // 创建订单对象
    Orders order = new Orders();
    BeanUtils.copyProperties(ordersSubmitDTO, order);
    order.setStatus(Orders.PENDING_PAYMENT); // 设置订单状态为待支付
    order.setUserId(userId); // 设置订单所属用户id
    order.setOrderTime(LocalDateTime.now());
    order.setPayStatus(Orders.UN_PAID);
    order.setNumber(String.valueOf(System.currentTimeMillis())); // 生成订单号
    order.setPhone(addressBook.getPhone());
    order.setConsignee(addressBook.getConsignee());
    // 向订单表插入一条数据
    orderMapper.insert(order);

    List<OrderDetail> orderDetails = new ArrayList<>();
    // 向订单详情表插入数据
    for (ShoppingCart item : list) {
      OrderDetail orderDetail = new OrderDetail();
      BeanUtils.copyProperties(item, orderDetail);
      orderDetail.setOrderId(order.getId());
      orderDetails.add(orderDetail);
    }
    orderDetailMapper.insertBatch(orderDetails);

    // 清空购物车数据
    shoppingCartMapper.clean(shoppingCart);

    // 封装返回结果
    OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
        .id(order.getId())
        .orderNumber(order.getNumber())
        .orderAmount(order.getAmount())
        .orderTime(order.getOrderTime())
        .build();

    return orderSubmitVO;
  }

  /**
   * 订单支付
   *
   * @param ordersPaymentDTO
   * @return
   */
  public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
    // 当前登录用户id
    Long userId = BaseContext.getCurrentId();
    User user = userMapper.getByOpenid(String.valueOf(userId));
    // 调用微信支付接口，生成预支付交易单
    JSONObject jsonObject = weChatPayUtil.pay(
        ordersPaymentDTO.getOrderNumber(), // 商户订单号
        new BigDecimal(0.01), // 支付金额，单位 元
        "苍穹外卖订单", // 商品描述
        user.getOpenid() // 微信用户的openid
    );
    if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
      throw new OrderBusinessException("该订单已支付");
    }
    OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
    vo.setPackageStr(jsonObject.getString("package"));
    return vo;
  }

  /**
   * 支付成功，修改订单状态
   *
   * @param outTradeNo
   */
  public void paySuccess(String outTradeNo) {
    // 当前登录用户id
    Long userId = BaseContext.getCurrentId();
    // 根据订单号查询当前用户的订单
    Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);
    // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
    Orders orders = Orders.builder()
        .id(ordersDB.getId())
        .status(Orders.TO_BE_CONFIRMED)
        .payStatus(Orders.PAID)
        .checkoutTime(LocalDateTime.now())
        .build();
    orderMapper.update(orders);
  }

}
