package com.sky.controller.admin;

import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/admin/report")
@RestController
@Slf4j
public class ReportController {
  @Autowired
  private ReportService reportService;

  @GetMapping("/turnoverStatistics")
  public Result<TurnoverReportVO> turnOver(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("turnover statistics");
    TurnoverReportVO statistics = reportService.turnOver(begin, end);
    return Result.success(statistics);
  }

  @GetMapping("/userStatistics")
  public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("user statistics");
    UserReportVO statistics = reportService.userStatistics(begin, end);
    return Result.success(statistics);
  }

  @GetMapping("/ordersStatistics")
  public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("orders statistics");
    OrderReportVO statistics = reportService.ordersStatistics(begin, end);
    return Result.success(statistics);
  }

  @GetMapping("/top10")
  public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("Top 10 sales");
    SalesTop10ReportVO statistics = reportService.top10(begin, end);
    return Result.success(statistics);
  }

  @GetMapping("/export")
  @ApiOperation("export business data report")
  public void export(HttpServletResponse response) {
    reportService.exportBusinessData(response);
  }
}
