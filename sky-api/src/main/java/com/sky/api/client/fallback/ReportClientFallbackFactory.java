package com.sky.api.client.fallback;

import java.time.LocalDate;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.ReportClient;
import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReportClientFallbackFactory implements FallbackFactory<ReportClient> {

  @Override
  public ReportClient create(Throwable cause) {
    return new ReportClient() {
      @Override
      public Result<TurnoverReportVO> turnOver(LocalDate begin, LocalDate end) {
        log.error("Feign调用ReportClient#turnOver失败, begin={}, end={}", begin, end, cause);
        return Result.error("报表服务暂时不可用");
      }

      @Override
      public Result<UserReportVO> userStatistics(LocalDate begin, LocalDate end) {
        log.error("Feign调用ReportClient#userStatistics失败, begin={}, end={}", begin, end, cause);
        return Result.error("报表服务暂时不可用");
      }

      @Override
      public Result<OrderReportVO> ordersStatistics(LocalDate begin, LocalDate end) {
        log.error("Feign调用ReportClient#ordersStatistics失败, begin={}, end={}", begin, end, cause);
        return Result.error("报表服务暂时不可用");
      }
    };
  }
}