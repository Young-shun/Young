package com.sky.api.client.fallback;

import java.time.LocalDateTime;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.WorkspaceClient;
import com.sky.result.Result;
import com.sky.vo.BusinessDataVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WorkspaceClientFallbackFactory implements FallbackFactory<WorkspaceClient> {

  @Override
  public WorkspaceClient create(Throwable cause) {
    return new WorkspaceClient() {
      @Override
      public Result<BusinessDataVO> getBusinessData(LocalDateTime begin, LocalDateTime end) {
        log.error("Feign调用WorkspaceClient#getBusinessData失败, begin={}, end={}", begin, end, cause);
        return Result.success(BusinessDataVO.builder()
            .turnover(0D)
            .validOrderCount(0)
            .orderCompletionRate(0D)
            .unitPrice(0D)
            .newUsers(0)
            .build());
      }
    };
  }
}