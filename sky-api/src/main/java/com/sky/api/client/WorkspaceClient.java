package com.sky.api.client;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.api.client.fallback.WorkspaceClientFallbackFactory;
import com.sky.result.Result;
import com.sky.vo.BusinessDataVO;

@FeignClient(name = "sky-take-out-workspace", fallbackFactory = WorkspaceClientFallbackFactory.class)
public interface WorkspaceClient {
  @GetMapping("/inner/workspace/businessData")
  Result<BusinessDataVO> getBusinessData(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end);
}
