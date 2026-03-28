package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson.JSON;
import com.sky.api.client.AddressClient;
import com.sky.api.client.ShoppingCartClient;
import com.sky.constant.MessageConstant;
import com.sky.constant.WebSocketMessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private OrderDetailMapper orderDetailMapper;
  @Autowired
  private ShoppingCartClient shoppingCartClient;
  @Autowired
  private AddressClient addressClient;
  @Autowired
  private WebSocketServer webSocketServer;

  @Override
  @Transactional
  public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
    Result<AddressBook> addressResult;
    try {
      addressResult = addressClient.getById(ordersSubmitDTO.getAddressBookId());
    } catch (Exception ex) {
      log.error("调用地址服务失败, addressBookId={}", ordersSubmitDTO.getAddressBookId(), ex);
      throw new OrderBusinessException("调用地址服务失败: " + ex.getMessage());
    }
    AddressBook addressBook = addressResult == null ? null : addressResult.getData();
    if (addressBook == null) {
      throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
    }

    Result<List<ShoppingCart>> cartResult;
    try {
      cartResult = shoppingCartClient.listItems();
    } catch (Exception ex) {
      log.error("调用购物车服务失败, userId={}", BaseContext.getCurrentId(), ex);
      throw new OrderBusinessException("调用购物车服务失败: " + ex.getMessage());
    }
    List<ShoppingCart> cartItems = cartResult == null ? null : cartResult.getData();
    if (cartItems == null || cartItems.isEmpty()) {
      throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
    }

    Long userId = BaseContext.getCurrentId();
    Orders order = new Orders();
    BeanUtils.copyProperties(ordersSubmitDTO, order);
    order.setUserId(userId);
    order.setStatus(Orders.PENDING_PAYMENT);
    order.setPayStatus(Orders.UN_PAID);
    order.setOrderTime(LocalDateTime.now());
    order.setNumber(String.valueOf(System.currentTimeMillis()));
    order.setPhone(addressBook.getPhone());
    order.setConsignee(addressBook.getConsignee());
    order.setAddress(buildAddress(addressBook));
    orderMapper.insert(order);

    for (ShoppingCart item : cartItems) {
      OrderDetail orderDetail = new OrderDetail();
      BeanUtils.copyProperties(item, orderDetail);
      orderDetail.setOrderId(order.getId());
      orderDetailMapper.insert(orderDetail);
    }

    shoppingCartClient.cleanCart();

    return OrderSubmitVO.builder()
        .id(order.getId())
        .orderNumber(order.getNumber())
        .orderAmount(order.getAmount())
        .orderTime(order.getOrderTime())
        .build();
  }

  @Override
  public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
    return OrderPaymentVO.builder()
        .timeStamp(String.valueOf(System.currentTimeMillis()))
        .nonceStr(ordersPaymentDTO.getOrderNumber())
        .signType("MD5")
        .paySign("mock-pay-sign")
        .packageStr("prepay_id=mock")
        .build();
  }

  @Override
  public void paySuccess(String outTradeNo) {
    Orders ordersDB = orderMapper.selectOne(new LambdaQueryWrapper<Orders>()
        .eq(Orders::getNumber, outTradeNo)
        .eq(Orders::getUserId, BaseContext.getCurrentId()));
    if (ordersDB == null) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }
    Orders orders = Orders.builder()
        .id(ordersDB.getId())
        .status(Orders.TO_BE_CONFIRMED)
        .payStatus(Orders.PAID)
        .checkoutTime(LocalDateTime.now())
        .build();
    orderMapper.updateById(orders);

    Map<String, Object> map = new HashMap<>();
    map.put("type", WebSocketMessageConstant.ORDER_REMINDER);
    map.put("orderId", orders.getId());
    map.put("content", WebSocketMessageConstant.ORDER_NUMBER_PREFIX + outTradeNo);
    webSocketServer.sendToAllClient(JSON.toJSONString(map));
  }

  @Override
  public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
    // 对齐 sky-server 设计：将当前登录用户写入查询DTO，再统一按DTO构建查询条件
    Long userId = BaseContext.getCurrentId();
    ordersPageQueryDTO.setUserId(userId);

    Page<Orders> page = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
    orderMapper.selectPage(page, new LambdaQueryWrapper<Orders>()
        .eq(ordersPageQueryDTO.getUserId() != null, Orders::getUserId, ordersPageQueryDTO.getUserId())
        .orderByDesc(Orders::getOrderTime));

    List<OrderVO> list = new ArrayList<>();
    if (page != null && page.getTotal() > 0) {
      for (Orders orders : page.getRecords()) {
        Long orderId = orders.getId();
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
            new LambdaQueryWrapper<OrderDetail>().eq(orderId != null && orderId > 0, OrderDetail::getOrderId, orderId));

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);

        list.add(orderVO);
      }
    }
    return new PageResult(page.getTotal(), list);
  }

  @Override
  public OrderVO getOrderDetail(Long id) {
    Orders orders = orderMapper.selectById(id);
    OrderVO orderVO = new OrderVO();
    BeanUtils.copyProperties(orders, orderVO);
    List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
        .eq(id != null && id > 0, OrderDetail::getOrderId, id));
    orderVO.setOrderDetailList(details);
    return orderVO;
  }

  @Override
  public void userCancelById(Long id) {
    Orders ordersDB = orderMapper.selectById(id);
    if (ordersDB == null) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }
    if (ordersDB.getStatus() > 2) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    if (Orders.TO_BE_CONFIRMED.equals(ordersDB.getStatus())) {
      orders.setPayStatus(Orders.REFUND);
    }
    orders.setStatus(Orders.CANCELLED);
    orders.setCancelReason("用户取消");
    orders.setCancelTime(LocalDateTime.now());
    orderMapper.updateById(orders);
  }

  @Override
  public void repetition(Long id) {
    Long userId = BaseContext.getCurrentId();
    List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
        .eq(id != null && id > 0, OrderDetail::getOrderId, id));
    for (OrderDetail detail : details) {
      ShoppingCart shoppingCart = new ShoppingCart();
      BeanUtils.copyProperties(detail, shoppingCart, "id");
      shoppingCart.setUserId(userId);
      shoppingCart.setCreateTime(LocalDateTime.now());
      shoppingCartClient.addItem(shoppingCart);
    }
  }

  @Override
  public OrderStatisticsVO statistics() {
    Long toBeConfirmed = count(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, Orders.TO_BE_CONFIRMED));
    Long confirmed = count(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, Orders.CONFIRMED));
    Long deliveryInProgress = count(
        new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, Orders.DELIVERY_IN_PROGRESS));
    OrderStatisticsVO vo = new OrderStatisticsVO();
    vo.setToBeConfirmed(toBeConfirmed.intValue());
    vo.setConfirmed(confirmed.intValue());
    vo.setDeliveryInProgress(deliveryInProgress.intValue());
    return vo;
  }

  @Override
  public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
    Orders orders = Orders.builder()
        .id(ordersConfirmDTO.getId())
        .status(Orders.CONFIRMED)
        .build();
    orderMapper.updateById(orders);
  }

  @Override
  public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
    Orders ordersDB = orderMapper.selectById(ordersRejectionDTO.getId());
    if (ordersDB == null || !Orders.TO_BE_CONFIRMED.equals(ordersDB.getStatus())) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    orders.setStatus(Orders.CANCELLED);
    orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
    orders.setCancelTime(LocalDateTime.now());
    orderMapper.updateById(orders);
  }

  @Override
  public void delivery(Long id) {
    Orders ordersDB = orderMapper.selectById(id);
    if (ordersDB == null || !Orders.CONFIRMED.equals(ordersDB.getStatus())) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(id);
    orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
    orderMapper.updateById(orders);
  }

  @Override
  public void complete(Long id) {
    Orders ordersDB = orderMapper.selectById(id);
    if (ordersDB == null || !Orders.DELIVERY_IN_PROGRESS.equals(ordersDB.getStatus())) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(id);
    orders.setStatus(Orders.COMPLETED);
    orders.setDeliveryTime(LocalDateTime.now());
    orderMapper.updateById(orders);
  }

  @Override
  public void reminder(Long id) {
    Orders orders = orderMapper.selectById(id);
    if (orders == null) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }

    String outTradeNo = orders.getNumber();
    if (!StringUtils.hasText(outTradeNo)) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("type", WebSocketMessageConstant.ORDER_URGE);
    map.put("orderId", id);
    map.put("content", WebSocketMessageConstant.ORDER_NUMBER_PREFIX + outTradeNo);
    webSocketServer.sendToAllClient(JSON.toJSONString(map));
  }

  @Override
  public PageResult queryOrderByPage(OrdersPageQueryDTO ordersPageQueryDTO) {
    Page<Orders> page = new Page<>(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>()
        .like(StringUtils.hasText(ordersPageQueryDTO.getNumber()), Orders::getNumber, ordersPageQueryDTO.getNumber())
        .eq(ordersPageQueryDTO.getStatus() != null, Orders::getStatus, ordersPageQueryDTO.getStatus())
        .like(StringUtils.hasText(ordersPageQueryDTO.getPhone()), Orders::getPhone, ordersPageQueryDTO.getPhone())
        .ge(ordersPageQueryDTO.getBeginTime() != null, Orders::getOrderTime, ordersPageQueryDTO.getBeginTime())
        .le(ordersPageQueryDTO.getEndTime() != null, Orders::getOrderTime, ordersPageQueryDTO.getEndTime())
        .orderByDesc(Orders::getOrderTime);
    page(page, queryWrapper);
    List<OrderVO> orderVOList = page.getRecords().stream().map(orders -> {
      OrderVO orderVO = new OrderVO();
      BeanUtils.copyProperties(orders, orderVO);
      List<OrderDetail> details = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
          .eq(OrderDetail::getOrderId, orders.getId()));
      String orderDishes = details.stream().map(d -> d.getName() + "*" + d.getNumber() + ";")
          .collect(Collectors.joining());
      orderVO.setOrderDishes(orderDishes);
      return orderVO;
    }).collect(Collectors.toList());
    return new PageResult(page.getTotal(), orderVOList);
  }

  private String buildAddress(AddressBook addressBook) {
    List<String> parts = new ArrayList<>();
    if (StringUtils.hasText(addressBook.getProvinceName())) {
      parts.add(addressBook.getProvinceName());
    }
    if (StringUtils.hasText(addressBook.getCityName())) {
      parts.add(addressBook.getCityName());
    }
    if (StringUtils.hasText(addressBook.getDistrictName())) {
      parts.add(addressBook.getDistrictName());
    }
    if (StringUtils.hasText(addressBook.getDetail())) {
      parts.add(addressBook.getDetail());
    }
    return parts.isEmpty() ? "" : String.join("", parts);
  }
}
