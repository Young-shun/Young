package com.sky.api.client;

import com.sky.entity.User;
import com.sky.api.client.fallback.UserClientFallbackFactory;
import com.sky.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "sky-take-out-user", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

  @GetMapping("/user/{id}")
  Result<User> getById(@PathVariable("id") Long id);

  @GetMapping("/inner/user/newCount")
  Result<Long> newCount(
      @RequestParam("begin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
      @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end);

  @GetMapping("/inner/user/totalCount")
  Result<Long> totalCount(@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end);

}
