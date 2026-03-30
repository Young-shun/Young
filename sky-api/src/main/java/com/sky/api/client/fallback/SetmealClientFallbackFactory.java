package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.SetmealClient;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SetmealClientFallbackFactory implements FallbackFactory<SetmealClient> {

  @Override
  public SetmealClient create(Throwable cause) {
    return new SetmealClient() {
      @Override
      public Setmeal getById(Long id) {
        log.error("Feign调用SetmealClient#getById失败, id={}", id, cause);
        return null;
      }

      @Override
      public Result<Long> countByStatus(Integer status) {
        log.error("Feign调用SetmealClient#countByStatus失败, status={}", status, cause);
        return Result.success(0L);
      }

      @Override
      public Result<String> syncCategory(Category category) {
        log.error("Feign调用SetmealClient#syncCategory失败, category={}", category, cause);
        return Result.error("套餐服务暂时不可用");
      }

      @Override
      public Result<String> deleteCategory(Long id) {
        log.error("Feign调用SetmealClient#deleteCategory失败, id={}", id, cause);
        return Result.error("套餐服务暂时不可用");
      }
    };
  }
}