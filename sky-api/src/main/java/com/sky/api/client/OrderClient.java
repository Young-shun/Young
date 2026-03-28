package com.sky.api.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.dto.GoodsSalesDTO;
import com.sky.result.Result;

@FeignClient(name = "sky-take-out-order")
public interface OrderClient {

  @GetMapping("/inner/order/count")
  Result<Long> count(@RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @RequestParam(value = "status", required = false) Integer status);

  @GetMapping("/inner/order/turnover")
  Result<Double> turnover(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @RequestParam("status") Integer status);

  @GetMapping("/inner/order/top10")
  Result<List<GoodsSalesDTO>> top10(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end);
}
