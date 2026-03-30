package com.sky.api.client.fallback;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.OrderClient;
import com.sky.dto.GoodsSalesDTO;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderClientFallbackFactory implements FallbackFactory<OrderClient> {

  @Override
  public OrderClient create(Throwable cause) {
    return new OrderClient() {
      @Override
      public Result<Long> count(LocalDateTime begin, LocalDateTime end, Integer status) {
        log.error("Feign调用OrderClient#count失败, begin={}, end={}, status={}", begin, end, status, cause);
        return Result.success(0L);
      }

      @Override
      public Result<Double> turnover(LocalDateTime begin, LocalDateTime end, Integer status) {
        log.error("Feign调用OrderClient#turnover失败, begin={}, end={}, status={}", begin, end, status, cause);
        return Result.success(0D);
      }

      @Override
      public Result<List<GoodsSalesDTO>> top10(LocalDateTime begin, LocalDateTime end) {
        log.error("Feign调用OrderClient#top10失败, begin={}, end={}", begin, end, cause);
        return Result.success(Collections.emptyList());
      }
    };
  }
}