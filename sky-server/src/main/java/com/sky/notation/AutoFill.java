package com.sky.notation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sky.enumeration.OperationType;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
  // 操作类型
  /**
   * 作用: 执行value相关逻辑。
   * 输入: 无。
   * 输出: OperationType。
   */
  OperationType value();

}
