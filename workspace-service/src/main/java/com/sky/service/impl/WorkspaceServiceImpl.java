package com.sky.service.impl;

import java.time.LocalDate;
import com.sky.service.WorkspaceService;
import com.sky.api.client.DishClient;
import com.sky.api.client.OrderClient;
import com.sky.api.client.ReportClient;
import com.sky.api.client.SetmealClient;
import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import com.sky.vo.SetmealOverViewVO;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

  @Autowired
  private ReportClient reportClient;
  @Autowired
  private OrderClient orderClient;
  @Autowired
  private DishClient dishClient;
  @Autowired
  private SetmealClient setmealClient;

  @Override
  /**
   * 作用: 执行getBusinessData相关逻辑。
   * 输入: LocalDateTime begin, LocalDateTime end。
   * 输出: BusinessDataVO。
   */
  public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
    LocalDate beginDate = begin.toLocalDate();
    LocalDate endDate = end.toLocalDate();

    Result<TurnoverReportVO> turnoverResult = reportClient.turnOver(beginDate, endDate);
    Result<OrderReportVO> orderResult = reportClient.ordersStatistics(beginDate, endDate);
    Result<UserReportVO> userResult = reportClient.userStatistics(beginDate, endDate);

    double turnover = sumDouble(turnoverResult == null || turnoverResult.getData() == null
        ? null
        : turnoverResult.getData().getTurnoverList());
    int validOrderCount = sumInt(orderResult == null || orderResult.getData() == null
        ? null
        : orderResult.getData().getValidOrderCountList());
    int totalOrderCount = sumInt(orderResult == null || orderResult.getData() == null
        ? null
        : orderResult.getData().getOrderCountList());
    int newUsers = sumInt(userResult == null || userResult.getData() == null
        ? null
        : userResult.getData().getNewUserList());

    double orderCompletionRate = totalOrderCount == 0 ? 0D : (double) validOrderCount / totalOrderCount;
    double unitPrice = validOrderCount == 0 ? 0D : turnover / validOrderCount;
    return BusinessDataVO.builder()
        .turnover(turnover)
        .validOrderCount(validOrderCount)
        .orderCompletionRate(orderCompletionRate)
        .unitPrice(unitPrice)
        .newUsers(newUsers)
        .build();
  }

  @Override
  /**
   * 作用: 执行getOrderOverView相关逻辑。
   * 输入: 无。
   * 输出: OrderOverViewVO。
   */
  public OrderOverViewVO getOrderOverView() {
    LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
    LocalDateTime end = LocalDateTime.now().plusDays(1).with(LocalTime.MIN);
    int waitingOrders = value(orderClient.count(begin, end, Orders.TO_BE_CONFIRMED));
    int deliveredOrders = value(orderClient.count(begin, end, Orders.CONFIRMED));
    int completedOrders = value(orderClient.count(begin, end, Orders.COMPLETED));
    int cancelledOrders = value(orderClient.count(begin, end, Orders.CANCELLED));
    int allOrders = value(orderClient.count(begin, end, null));
    return OrderOverViewVO.builder()
        .waitingOrders(waitingOrders)
        .deliveredOrders(deliveredOrders)
        .completedOrders(completedOrders)
        .cancelledOrders(cancelledOrders)
        .allOrders(allOrders)
        .build();
  }

  @Override
  /**
   * 作用: 执行getDishOverView相关逻辑。
   * 输入: 无。
   * 输出: DishOverViewVO。
   */
  public DishOverViewVO getDishOverView() {
    int sold = value(dishClient.countByStatus(StatusConstant.ENABLE));
    int discontinued = value(dishClient.countByStatus(StatusConstant.DISABLE));
    return DishOverViewVO.builder().sold(sold).discontinued(discontinued).build();
  }

  @Override
  /**
   * 作用: 执行getSetmealOverView相关逻辑。
   * 输入: 无。
   * 输出: SetmealOverViewVO。
   */
  public SetmealOverViewVO getSetmealOverView() {
    int sold = value(setmealClient.countByStatus(StatusConstant.ENABLE));
    int discontinued = value(setmealClient.countByStatus(StatusConstant.DISABLE));
    return SetmealOverViewVO.builder().sold(sold).discontinued(discontinued).build();
  }

  /**
   * 作用: 执行value相关逻辑。
   * 输入: Result<Long> result。
   * 输出: int。
   */
  private int value(Result<Long> result) {
    return result == null || result.getData() == null ? 0 : result.getData().intValue();
  }

  /**
   * 作用: 执行lastInt相关逻辑。
   * 输入: String csv。
   * 输出: int。
   */
  private int sumInt(String csv) {
    if (csv == null || csv.isBlank()) {
      return 0;
    }
    int sum = 0;
    String[] arr = csv.split(",");
    for (String item : arr) {
      String value = item.trim();
      if (!value.isEmpty()) {
        sum += Integer.parseInt(value);
      }
    }
    return sum;
  }

  /**
   * 作用: 执行lastDouble相关逻辑。
   * 输入: String csv。
   * 输出: double。
   */
  private double sumDouble(String csv) {
    if (csv == null || csv.isBlank()) {
      return 0D;
    }
    double sum = 0D;
    String[] arr = csv.split(",");
    for (String item : arr) {
      String value = item.trim();
      if (!value.isEmpty()) {
        sum += Double.parseDouble(value);
      }
    }
    return sum;
  }
}
