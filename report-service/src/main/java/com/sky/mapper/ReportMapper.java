package com.sky.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@Mapper
public interface ReportMapper {
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
  List<SalesTop10ReportVO> top10(LocalDate begin, LocalDate end);
}
