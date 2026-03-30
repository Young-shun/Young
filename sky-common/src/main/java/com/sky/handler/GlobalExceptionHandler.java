package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLIntegrityConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler
  /**
   * 作用: 执行exceptionHandler相关逻辑。
   * 输入: BaseException ex。
   * 输出: Result。
   */
  public Result exceptionHandler(BaseException ex) {
    log.error("异常信息：{}", ex.getMessage());
    return Result.error(ex.getMessage());
  }

  @ExceptionHandler
  /**
   * 作用: 执行exceptionHandler相关逻辑。
   * 输入: SQLIntegrityConstraintViolationException ex。
   * 输出: Result。
   */
  public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
    log.error("异常信息：{}", ex.getMessage());
    if (ex.getMessage().contains("Duplicate entry")) {
      String[] split = ex.getMessage().split(" ");
      String msg = split[2] + MessageConstant.ALREADY_EXISTS;
      return Result.error(msg);
    }
    return Result.error(MessageConstant.UNKNOWN_ERROR);
  }
}
