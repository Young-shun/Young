package com.sky.service;

import java.time.LocalDate;

import javax.servlet.http.HttpServletResponse;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface ReportService {
  /**
   * 销售额统计
   * 
   * @param begin
   * @param end
   * @return
   */
  TurnoverReportVO turnOver(LocalDate begin, LocalDate end);

  /**
   * 用户统计
   * 
   * @param begin
   * @param end
   * @return
   */
  UserReportVO userStatistics(LocalDate begin, LocalDate end);

  /**
   * 订单统计
   * 
   * @param begin
   * @param end
   * @return
   */
  OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

  /**
   * Top 10 订单统计
   * 
   * @param begin
   * @param end
   * @return
   */
  SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

  /**
   * 作用: 执行exportBusinessData相关逻辑。
   * 输入: HttpServletResponse response。
   * 输出: 无。
   */
  void exportBusinessData(HttpServletResponse response);

}
