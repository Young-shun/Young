package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

@Configuration
public class MybatisConfiguration {
  @Bean
  /**
   * 作用: 执行mybatisPlusInterceptor相关逻辑。
   * 输入: 无。
   * 输出: MybatisPlusInterceptor。
   */
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    // 初始化核心插件
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加分页插件
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }
}
