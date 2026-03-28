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
  TurnoverReportVO turnOver(LocalDate begin, LocalDate end);
  UserReportVO userStatistics(LocalDate begin, LocalDate end);
  OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
  List<SalesTop10ReportVO> top10(LocalDate begin, LocalDate end);
}
