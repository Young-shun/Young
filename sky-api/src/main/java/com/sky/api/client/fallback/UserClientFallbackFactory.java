package com.sky.api.client.fallback;

import java.time.LocalDateTime;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.UserClient;
import com.sky.entity.User;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

  @Override
  public UserClient create(Throwable cause) {
    return new UserClient() {
      @Override
      public Result<User> getById(Long id) {
        log.error("Feign调用UserClient#getById失败, id={}", id, cause);
        return Result.error("用户服务暂时不可用");
      }

      @Override
      public Result<Long> newCount(LocalDateTime begin, LocalDateTime end) {
        log.error("Feign调用UserClient#newCount失败, begin={}, end={}", begin, end, cause);
        return Result.success(0L);
      }

      @Override
      public Result<Long> totalCount(LocalDateTime end) {
        log.error("Feign调用UserClient#totalCount失败, end={}", end, cause);
        return Result.success(0L);
      }
    };
  }
}