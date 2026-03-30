package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.api.client.OrderClient;
import com.sky.api.client.UserClient;
import com.sky.api.client.WorkspaceClient;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Service
public class ReportServiceImpl implements ReportService {

  @Autowired
  private OrderClient orderClient;

  @Autowired
  private UserClient userClient;

  @Autowired
  private WorkspaceClient workspaceClient;

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
        .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
        .turnoverList(String.join(",", turnoverList))
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
        .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
        .totalUserList(String.join(",", totalUserList))
        .newUserList(String.join(",", newUserList))
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
        .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
        .orderCountList(String.join(",", orderCountList))
        .validOrderCountList(String.join(",", validOrderCountList))
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
    LocalDate begin = LocalDate.now().minusDays(30);
    LocalDate end = LocalDate.now().minusDays(1);

    BusinessDataVO summaryData = fetchBusinessData(LocalDateTime.of(begin, LocalTime.MIN),
        LocalDateTime.of(end, LocalTime.MAX));

    InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
    if (templateStream == null) {
      throw new RuntimeException("未找到报表模板文件: template/运营数据报表模板.xlsx");
    }

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=business-report.xlsx");

    try (InputStream inputStream = templateStream;
        XSSFWorkbook excel = new XSSFWorkbook(inputStream);
        ServletOutputStream outputStream = response.getOutputStream()) {

      XSSFSheet sheet = excel.getSheet("Sheet1");

      sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
      sheet.getRow(3).getCell(2).setCellValue(summaryData.getTurnover());
      sheet.getRow(3).getCell(4).setCellValue(summaryData.getOrderCompletionRate());
      sheet.getRow(3).getCell(6).setCellValue(summaryData.getNewUsers());
      sheet.getRow(4).getCell(2).setCellValue(summaryData.getValidOrderCount());
      sheet.getRow(4).getCell(4).setCellValue(summaryData.getUnitPrice());

      for (int i = 0; i < 30; i++) {
        LocalDate date = begin.plusDays(i);
        BusinessDataVO dailyData = fetchBusinessData(LocalDateTime.of(date, LocalTime.MIN),
            LocalDateTime.of(date, LocalTime.MAX));

        sheet.getRow(7 + i).getCell(1).setCellValue(date.toString());
        sheet.getRow(7 + i).getCell(2).setCellValue(dailyData.getTurnover());
        sheet.getRow(7 + i).getCell(3).setCellValue(dailyData.getValidOrderCount());
        sheet.getRow(7 + i).getCell(4).setCellValue(dailyData.getOrderCompletionRate());
        sheet.getRow(7 + i).getCell(5).setCellValue(dailyData.getUnitPrice());
        sheet.getRow(7 + i).getCell(6).setCellValue(dailyData.getNewUsers());
      }
      excel.write(outputStream);
      outputStream.flush();
    } catch (IOException e) {
      throw new RuntimeException("导出运营数据报表失败", e);
    }
  }

  private BusinessDataVO fetchBusinessData(LocalDateTime begin, LocalDateTime end) {
    Result<BusinessDataVO> result = workspaceClient.getBusinessData(begin, end);
    if (result == null || result.getData() == null) {
      return BusinessDataVO.builder()
          .turnover(0D)
          .validOrderCount(0)
          .orderCompletionRate(0D)
          .unitPrice(0D)
          .newUsers(0)
          .build();
    }
    return result.getData();
  }
}
