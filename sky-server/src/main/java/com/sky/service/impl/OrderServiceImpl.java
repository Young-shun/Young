package com.sky.service.impl;

import java.math.BigDecimal;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.PageDTO;
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
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.DishVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

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
  @Autowired
  private WebSocketServer webSocketServer;

  @Value("${sky.shop.address}")
  private String shopAddress;

  @Value("${sky.baidu.ak}")
  private String ak;

  /**
   * 提交订单
   */
  @Override
  @Transactional
  public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
    // 先判断地址簿和购物车是否为空
    AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
    if (addressBook == null) {
      throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
    }
    // 检查用户的收货地址是否超出配送范围
    checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());
    // 获取当前用户id
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
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("user_id", userId);
    shoppingCartMapper.deleteByMap(columnMap);

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
   * 检查客户的收货地址是否超出配送范围
   * 
   * @param address
   */
  private void checkOutOfRange(String address) {
    Map map = new HashMap();
    map.put("address", shopAddress);
    map.put("output", "json");
    map.put("ak", ak);
    // 获取店铺的经纬度坐标
    String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
    JSONObject jsonObject = JSON.parseObject(shopCoordinate);
    if (!jsonObject.getString("status").equals("0")) {
      throw new OrderBusinessException("店铺地址解析失败");
    }
    // 数据解析
    JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
    String lat = location.getString("lat");
    String lng = location.getString("lng");
    // 店铺经纬度坐标
    String shopLngLat = lat + "," + lng;
    map.put("address", address);
    // 获取用户收货地址的经纬度坐标
    String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);
    jsonObject = JSON.parseObject(userCoordinate);
    if (!jsonObject.getString("status").equals("0")) {
      throw new OrderBusinessException("收货地址解析失败");
    }
    // 数据解析
    location = jsonObject.getJSONObject("result").getJSONObject("location");
    lat = location.getString("lat");
    lng = location.getString("lng");
    // 用户收货地址经纬度坐标
    String userLngLat = lat + "," + lng;
    map.put("origin", shopLngLat);
    map.put("destination", userLngLat);
    map.put("steps_info", "0");
    // 路线规划
    String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);
    jsonObject = JSON.parseObject(json);
    if (!jsonObject.getString("status").equals("0")) {
      throw new OrderBusinessException("配送路线规划失败");
    }
    // 数据解析
    JSONObject result = jsonObject.getJSONObject("result");
    JSONArray jsonArray = (JSONArray) result.get("routes");
    Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");
    if (distance > 50000000) {
      // 配送距离超过50000000米
      throw new OrderBusinessException("超出配送范围");
    }
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
    // 向客户端推送数据
    Map map = new HashMap();
    map.put("type", 1);
    map.put("orderId", orders.getId());
    map.put("content", "订单号：" + outTradeNo);
    // 通过WebSocket实现来单提醒，向客户端浏览器推送消息
    webSocketServer.sendToAllClient(JSON.toJSONString(map));
  }

  @Override
  public void reminder(Long id) {
    // 通过WebSocket实现来单提醒，向客户端浏览器推送消息
    String outTradeNo = orderMapper.getNumberById(id);
    if (outTradeNo == null) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }
    Map<String, Object> map = new HashMap<>();
    map.put("type", 2);
    map.put("orderId", id);
    map.put("content", "订单号：" + outTradeNo);
    // 通过WebSocket实现来单提醒，向客户端浏览器推送消息
    webSocketServer.sendToAllClient(JSON.toJSONString(map));
  }

  /**
   * 获取用户历史订单
   */
  @Override
  public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
    // 当前登录用户id
    Long userId = BaseContext.getCurrentId();
    ordersPageQueryDTO.setUserId(userId);
    // 1.构建条件
    Page<Orders> page = ordersPageQueryDTO.toMpPageDefaultSortByCreateTimeDesc();
    // 2.查询
    page(page);

    List<OrderVO> list = new ArrayList();
    // 查询出订单明细，并封装入OrderVO进行响应
    if (page != null && page.getTotal() > 0) {
      for (Orders orders : page.getRecords()) {
        Long orderId = orders.getId();// 订单id

        // 查询订单明细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);

        list.add(orderVO);
      }
    }
    return new PageResult(page.getTotal(), list);
  }

  /**
   * 获取订单详情
   */
  @Override
  public OrderVO getOrderDetail(Long id) {
    OrderVO orderVO = orderMapper.getOrderDetail(id);
    // 查询订单明细
    List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
    orderVO.setOrderDetailList(orderDetails);
    return orderVO;
  }

  /**
   * 用户取消订单
   *
   * @param id
   */
  public void userCancelById(Long id) {
    // 根据id查询订单
    Orders ordersDB = orderMapper.getOrderDetail(id);
    // 校验订单是否存在
    if (ordersDB == null) {
      throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
    }
    // 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
    if (ordersDB.getStatus() > 2) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    // 订单处于待接单状态下取消，需要进行退款
    if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
      // 支付状态修改为 退款
      orders.setPayStatus(Orders.REFUND);
    }
    // 更新订单状态、取消原因、取消时间
    orders.setStatus(Orders.CANCELLED);
    orders.setCancelReason("用户取消");
    orders.setCancelTime(LocalDateTime.now());
    orderMapper.update(orders);
  }

  /**
   * 再来一单
   *
   * @param id
   */
  public void repetition(Long id) {
    // 查询当前用户id
    Long userId = BaseContext.getCurrentId();

    // 根据订单id查询当前订单详情
    List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

    // 将订单详情对象转换为购物车对象
    List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
      ShoppingCart shoppingCart = new ShoppingCart();

      // 将原订单详情里面的菜品信息重新复制到购物车对象中
      BeanUtils.copyProperties(x, shoppingCart, "id");
      shoppingCart.setUserId(userId);
      shoppingCart.setCreateTime(LocalDateTime.now());
      shoppingCartMapper.insert(shoppingCart);
      return shoppingCart;
    }).collect(Collectors.toList());
  }

  /**
   * 订单搜索
   *
   * @param ordersPageQueryDTO
   * @return
   */
  public PageResult queryOrderByPage(OrdersPageQueryDTO ordersPageQueryDTO) {

    // 1.构建条件
    Page<Orders> page = ordersPageQueryDTO.toMpPageDefaultSortByCreateTimeDesc();
    // 2.查询
    page(page);
    // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
    List<OrderVO> orderVOList = getOrderVOList(page);

    return new PageResult(page.getTotal(), orderVOList);
  }

  /**
   * 将Orders转化为OrderVO
   */
  private List<OrderVO> getOrderVOList(Page<Orders> page) {
    // 需要返回订单菜品信息，自定义OrderVO响应结果
    List<OrderVO> orderVOList = new ArrayList<>();

    List<Orders> ordersList = page.getRecords();
    if (!CollectionUtils.isEmpty(ordersList)) {
      for (Orders orders : ordersList) {
        // 将共同字段复制到OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        String orderDishes = getOrderDishesStr(orders);

        // 将订单菜品信息封装到orderVO中，并添加到orderVOList
        orderVO.setOrderDishes(orderDishes);
        orderVOList.add(orderVO);
      }
    }
    return orderVOList;
  }

  /**
   * 根据订单id获取菜品信息字符串
   *
   * @param orders
   * @return
   */
  private String getOrderDishesStr(Orders orders) {
    // 查询订单菜品详情信息（订单中的菜品和数量）
    List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

    // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
    List<String> orderDishList = orderDetailList.stream().map(x -> {
      String orderDish = x.getName() + "*" + x.getNumber() + ";";
      return orderDish;
    }).collect(Collectors.toList());

    // 将该订单对应的所有菜品信息拼接在一起
    return String.join("", orderDishList);
  }

  /**
   * 各个状态的订单数量统计
   *
   * @return
   */
  public OrderStatisticsVO statistics() {
    // 根据状态，分别查询出待接单、待派送、派送中的订单数量
    Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
    Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
    Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

    // 将查询出的数据封装到orderStatisticsVO中响应
    OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
    orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
    orderStatisticsVO.setConfirmed(confirmed);
    orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
    return orderStatisticsVO;
  }

  /**
   * 接单
   *
   * @param ordersConfirmDTO
   */
  public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
    Orders orders = Orders.builder()
        .id(ordersConfirmDTO.getId())
        .status(Orders.CONFIRMED)
        .build();
    orderMapper.update(orders);
  }

  /**
   * 拒单
   *
   * @param ordersRejectionDTO
   */
  public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
    // 根据id查询订单
    Orders ordersDB = orderMapper.getOrderDetail(ordersRejectionDTO.getId());
    // 订单只有存在且状态为2（待接单）才可以拒单
    if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    orders.setStatus(Orders.CANCELLED);
    orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
    orders.setCancelTime(LocalDateTime.now());
    orderMapper.update(orders);
  }

  /**
   * 派送订单
   *
   * @param id
   */
  public void delivery(Long id) {
    // 根据id查询订单
    Orders ordersDB = orderMapper.getOrderDetail(id);
    // 校验订单是否存在，并且状态为3
    if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    // 更新订单状态,状态转为派送中
    orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
    orderMapper.update(orders);
  }

  /**
   * 完成订单
   *
   * @param id
   */
  public void complete(Long id) {
    // 根据id查询订单
    Orders ordersDB = orderMapper.getOrderDetail(id);
    // 校验订单是否存在，并且状态为4
    if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
    }
    Orders orders = new Orders();
    orders.setId(ordersDB.getId());
    // 更新订单状态,状态转为完成
    orders.setStatus(Orders.COMPLETED);
    orders.setDeliveryTime(LocalDateTime.now());
    orderMapper.update(orders);
  }

}
