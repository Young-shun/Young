package com.sky.service;

import java.time.LocalDate;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface ReportService {
  /**
   * 作用: 执行turnOver相关逻辑。
   * 输入: LocalDate begin, LocalDate end。
   * 输出: TurnoverReportVO。
   */
  TurnoverReportVO turnOver(LocalDate begin, LocalDate end);
  /**
   * 作用: 执行userStatistics相关逻辑。
   * 输入: LocalDate begin, LocalDate end。
   * 输出: UserReportVO。
   */
  UserReportVO userStatistics(LocalDate begin, LocalDate end);
  /**
   * 作用: 执行ordersStatistics相关逻辑。
   * 输入: LocalDate begin, LocalDate end。
   * 输出: OrderReportVO。
   */
  OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
  /**
   * 作用: 执行top10相关逻辑。
   * 输入: LocalDate begin, LocalDate end。
   * 输出: SalesTop10ReportVO。
   */
  SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
  /**
   * 作用: 执行exportBusinessData相关逻辑。
   * 输入: javax.servlet.http.HttpServletResponse response。
   * 输出: 无。
   */
  void exportBusinessData(javax.servlet.http.HttpServletResponse response);
}
