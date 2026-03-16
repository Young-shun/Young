package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Service
public class ReportServiceImpl implements ReportService {
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private WorkspaceService workspaceService;

  /**
   * 统计区间内的营业额
   */
  @Override
  public TurnoverReportVO turnOver(LocalDate begin, LocalDate end) {
    TurnoverReportVO report = new TurnoverReportVO();
    // 统计区间内的营业额
    List<LocalDate> dateList = new ArrayList<>();

    String turnoverList = "";
    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
      Double dailyTurnover = orderMapper.getTotalAmount(beginTime, endTime, Orders.COMPLETED);
      if (dailyTurnover == null) {
        dailyTurnover = 0.0;
      }
      turnoverList += dailyTurnover + ",";
    }
    report.setDateList(StringUtils.join(dateList, ","));
    report.setTurnoverList(turnoverList);
    return report;
  }

  @Override
  public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
    UserReportVO user = new UserReportVO();

    List<LocalDate> dateList = new ArrayList<>();

    String totalUserList = "";
    String newUserList = "";
    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

      Long dailytotalUserList = Db.lambdaQuery(User.class)
          .lt(endTime != null, User::getCreateTime, endTime)
          .count();
      Long dailyNewUserList = Db.lambdaQuery(User.class)
          .ge(beginTime != null, User::getCreateTime, beginTime)
          .lt(endTime != null, User::getCreateTime, endTime)
          .count();

      if (dailytotalUserList == null) {
        dailytotalUserList = 0L;
      }
      totalUserList += dailytotalUserList + ",";

      if (dailyNewUserList == null) {
        dailyNewUserList = 0L;
      }
      newUserList += dailyNewUserList + ",";
    }
    user.setDateList(StringUtils.join(dateList, ","));
    user.setTotalUserList(totalUserList);
    user.setNewUserList(newUserList);
    return user;

  }

  /**
   * 订单统计
   */
  @Override
  public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
    OrderReportVO order = new OrderReportVO();

    List<LocalDate> dateList = new ArrayList<>();

    String orderCountList = "";
    String validOrderCountList = "";
    Integer totalOrderCount = 0;
    Integer validOrderCount = 0;
    Double orderCompletionRate = 0.0;

    for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
      dateList.add(date);
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
      Long dailyOrderCountList = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
          .between(Orders::getOrderTime, beginTime, endTime));
      Long dailyValidOrderCountList = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
          .between(Orders::getOrderTime, beginTime, endTime)
          .eq(Orders::getStatus, Orders.COMPLETED));

      if (dailyOrderCountList == null) {
        dailyOrderCountList = 0L;
      }
      totalOrderCount += dailyOrderCountList.intValue();
      orderCountList += dailyOrderCountList + ",";

      if (dailyValidOrderCountList == null) {
        dailyValidOrderCountList = 0L;
      }
      validOrderCountList += dailyValidOrderCountList + ",";
      validOrderCount += dailyValidOrderCountList.intValue();
    }
    orderCompletionRate = totalOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;
    order.setDateList(StringUtils.join(dateList, ","));
    order.setTotalOrderCount(totalOrderCount);
    order.setValidOrderCountList(validOrderCountList);
    order.setOrderCountList(orderCountList);
    order.setValidOrderCount(validOrderCount);
    order.setOrderCompletionRate(orderCompletionRate);
    return order;
  }

  /**
   * Top 10 订单统计
   */
  @Override
  public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
    LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
    List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);
    String nameList = StringUtils
        .join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
    String numberList = StringUtils
        .join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");
    return SalesTop10ReportVO.builder()
        .nameList(nameList)
        .numberList(numberList)
        .build();
  }

  /**
   * 导出近30天的运营数据报表
   */
  @Override
  public void exportBusinessData(HttpServletResponse response) {
    LocalDate begin = LocalDate.now().minusDays(30);
    LocalDate end = LocalDate.now().minusDays(1);
    // 查询概览运营数据，提供给Excel模板文件
    BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
        LocalDateTime.of(end, LocalTime.MAX));
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
    // 使用工具类生成Excel文件并通过响应输出
    try {
      // 基于提供好的模板文件创建一个新的Excel表格对象
      XSSFWorkbook excel = new XSSFWorkbook(inputStream);
      // 获得Excel文件中的一个Sheet页
      XSSFSheet sheet = excel.getSheet("Sheet1");
      // 设置标题
      sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
      // 设置主题内容，第四行
      sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
      sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
      sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
      // 设置主题内容，第五行
      sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
      sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
      for (int i = 0; i < 30; i++) {
        LocalDate date = begin.plusDays(i);
        // 准备明细数据
        businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN),
            LocalDateTime.of(date, LocalTime.MAX));
        sheet.getRow(7 + i).getCell(1).setCellValue(date);
        sheet.getRow(7 + i).getCell(2).setCellValue(businessData.getTurnover());
        sheet.getRow(7 + i).getCell(3).setCellValue(businessData.getValidOrderCount());
        sheet.getRow(7 + i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
        sheet.getRow(7 + i).getCell(5).setCellValue(businessData.getUnitPrice());
        sheet.getRow(7 + i).getCell(6).setCellValue(businessData.getNewUsers());
      }
      ServletOutputStream outputStream = response.getOutputStream();
      response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
      response.setHeader("Content-Disposition", "attachment; filename=运营数据报表.xlsx");
      excel.write(outputStream);
      // 关闭资源
      outputStream.flush();
      outputStream.close();
      excel.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
