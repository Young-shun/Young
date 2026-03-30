package com.sky.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageQuery {

  private int page;

  private int pageSize;
  private String sortBy;
  private Boolean isAsc;

  /**
   * 作用: 执行toMpPage相关逻辑。
   * 输入: OrderItem... orders。
   * 输出: <T> Page<T>。
   */
  public <T> Page<T> toMpPage(OrderItem... orders) {
    // 1.分页条件
    Page<T> p = Page.of(page, pageSize);
    // 2.排序条件
    // 2.1.先看前端有没有传排序字段
    if (sortBy != null) {
      p.addOrder(new OrderItem(sortBy, isAsc));
      return p;
    }
    // 2.2.再看有没有手动指定排序字段
    if (orders != null) {
      p.addOrder(orders);
    }
    return p;
  }

  /**
   * 作用: 执行toMpPage相关逻辑。
   * 输入: String defaultSortBy, boolean isAsc。
   * 输出: <T> Page<T>。
   */
  public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
    return this.toMpPage(new OrderItem(defaultSortBy, isAsc));
  }

  /**
   * 作用: 执行toMpPageDefaultSortByCreateTimeDesc相关逻辑。
   * 输入: 无。
   * 输出: <T> Page<T>。
   */
  public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
    return toMpPage("create_time", false);
  }

  /**
   * 作用: 执行toMpPageDefaultSortByUpdateTimeDesc相关逻辑。
   * 输入: 无。
   * 输出: <T> Page<T>。
   */
  public <T> Page<T> toMpPageDefaultSortByUpdateTimeDesc() {
    return toMpPage("update_time", false);
  }
}
