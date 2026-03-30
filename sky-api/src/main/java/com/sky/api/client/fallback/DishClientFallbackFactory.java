package com.sky.api.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sky.api.client.DishClient;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DishClientFallbackFactory implements FallbackFactory<DishClient> {

  @Override
  public DishClient create(Throwable cause) {
    return new DishClient() {
      @Override
      public Dish getById(Long id) {
        log.error("Feign调用DishClient#getById失败, id={}", id, cause);
        return null;
      }

      @Override
      public Result<Long> countByStatus(Integer status) {
        log.error("Feign调用DishClient#countByStatus失败, status={}", status, cause);
        return Result.success(0L);
      }

      @Override
      public Result<String> syncCategory(Category category) {
        log.error("Feign调用DishClient#syncCategory失败, category={}", category, cause);
        return Result.error("菜品服务暂时不可用");
      }

      @Override
      public Result<String> deleteCategory(Long id) {
        log.error("Feign调用DishClient#deleteCategory失败, id={}", id, cause);
        return Result.error("菜品服务暂时不可用");
      }
    };
  }
}