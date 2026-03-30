package com.sky.handler;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
  @Override
  /**
   * 作用: 执行insertFill相关逻辑。
   * 输入: MetaObject metaObject。
   * 输出: 无。
   */
  public void insertFill(MetaObject metaObject) {
    // 插入时自动填充时间
    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    // 也可以从 SecurityContext 获取当前用户 ID 填充
    // this.strictInsertFill(metaObject, "createBy", Long.class, 1L);
  }

  @Override
  /**
   * 作用: 执行updateFill相关逻辑。
   * 输入: MetaObject metaObject。
   * 输出: 无。
   */
  public void updateFill(MetaObject metaObject) {
    // 更新时自动填充
    this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
  }
}
