package com.sky.api.client;

import java.time.LocalDate;

import com.sky.api.client.fallback.ReportClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sky.result.Result;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

@FeignClient(name = "sky-take-out-report", fallbackFactory = ReportClientFallbackFactory.class)
public interface ReportClient {
    @GetMapping("/admin/report/turnoverStatistics")
    Result<TurnoverReportVO> turnOver(
            @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate begin,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end);

    @GetMapping("/admin/report/userStatistics")
    Result<UserReportVO> userStatistics(
            @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate begin,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end);

    @GetMapping("/admin/report/ordersStatistics")
    Result<OrderReportVO> ordersStatistics(
            @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate begin,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end);
}
