package com.sky.controller.inner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.result.Result;

@RestController
@RequestMapping("/inner/order")
public class InnerOrderController {

  @Autowired
  private OrderMapper orderMapper;

  @GetMapping("/count")
  public Result<Long> count(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @RequestParam(value = "status", required = false) Integer status) {
    Long total = orderMapper.selectCount(new LambdaQueryWrapper<Orders>()
        .ge(begin != null, Orders::getOrderTime, begin)
        .lt(end != null, Orders::getOrderTime, end)
        .eq(status != null, Orders::getStatus, status));
    return Result.success(total == null ? 0L : total);
  }

  @GetMapping("/turnover")
  public Result<Double> turnover(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @RequestParam("status") Integer status) {
    Double amount = orderMapper.getTotalAmount(begin, end, status);
    return Result.success(amount == null ? 0D : amount);
  }

  @GetMapping("/top10")
  public Result<List<GoodsSalesDTO>> top10(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
    List<GoodsSalesDTO> list = orderMapper.getSalesTop10(begin, end);
    return Result.success(list == null ? Collections.emptyList() : list);
  }
}
