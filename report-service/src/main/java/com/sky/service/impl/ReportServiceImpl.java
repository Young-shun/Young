package com.sky.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sky.service.ReportService;
import com.sky.api.client.OrderClient;
import com.sky.api.client.UserClient;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import java.time.LocalDate;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {

  @Autowired
  private OrderClient orderClient;
  @Autowired
  private UserClient userClient;

  @Override
  public TurnoverReportVO turnOver(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    List<String> turnoverList = new ArrayList<>();
    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
      Result<Double> turnoverResult = orderClient.turnover(beginTime, endTime, Orders.COMPLETED);
      Double turnover = turnoverResult == null || turnoverResult.getData() == null ? 0D : turnoverResult.getData();
      turnoverList.add(String.valueOf(turnover));
    }
    return TurnoverReportVO.builder()
        .dateList(StringUtils.join(dateList, ","))
        .turnoverList(StringUtils.join(turnoverList, ","))
        .build();
  }

  @Override
  public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    List<String> totalUserList = new ArrayList<>();
    List<String> newUserList = new ArrayList<>();
    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
      Result<Long> totalResult = userClient.totalCount(endTime);
      Result<Long> newResult = userClient.newCount(beginTime, endTime);
      totalUserList
          .add(String.valueOf(totalResult == null || totalResult.getData() == null ? 0L : totalResult.getData()));
      newUserList.add(String.valueOf(newResult == null || newResult.getData() == null ? 0L : newResult.getData()));
    }
    return UserReportVO.builder()
        .dateList(StringUtils.join(dateList, ","))
        .totalUserList(StringUtils.join(totalUserList, ","))
        .newUserList(StringUtils.join(newUserList, ","))
        .build();
  }

  @Override
  public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    List<String> orderCountList = new ArrayList<>();
    List<String> validOrderCountList = new ArrayList<>();
    int totalOrderCount = 0;
    int validOrderCount = 0;
    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
      Result<Long> totalResult = orderClient.count(beginTime, endTime, null);
      Result<Long> validResult = orderClient.count(beginTime, endTime, Orders.COMPLETED);
      int total = totalResult == null || totalResult.getData() == null ? 0 : totalResult.getData().intValue();
      int valid = validResult == null || validResult.getData() == null ? 0 : validResult.getData().intValue();
      totalOrderCount += total;
      validOrderCount += valid;
      orderCountList.add(String.valueOf(total));
      validOrderCountList.add(String.valueOf(valid));
    }
    double orderCompletionRate = totalOrderCount == 0 ? 0D : (double) validOrderCount / totalOrderCount;
    return OrderReportVO.builder()
        .dateList(StringUtils.join(dateList, ","))
        .orderCountList(StringUtils.join(orderCountList, ","))
        .validOrderCountList(StringUtils.join(validOrderCountList, ","))
        .totalOrderCount(totalOrderCount)
        .validOrderCount(validOrderCount)
        .orderCompletionRate(orderCompletionRate)
        .build();
  }

  @Override
  public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
    LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(end.plusDays(1), LocalTime.MIN);
    Result<List<GoodsSalesDTO>> result = orderClient.top10(beginTime, endTime);
    List<GoodsSalesDTO> list = result == null || result.getData() == null ? new ArrayList<>() : result.getData();
    String nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));
    String numberList = list.stream().map(item -> String.valueOf(item.getNumber())).collect(Collectors.joining(","));
    return SalesTop10ReportVO.builder()
        .nameList(nameList)
        .numberList(numberList)
        .build();
  }

  @Override
  public void exportBusinessData(HttpServletResponse response) {
    // no-op for now
  }
}
