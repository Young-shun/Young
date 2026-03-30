package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
public class MvcConfig implements WebMvcConfigurer {
  @Override
  /**
   * 作用: 执行addInterceptors相关逻辑。
   * 输入: InterceptorRegistry registry。
   * 输出: 无。
   */
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new JwtTokenUserInterceptor());
    registry.addInterceptor(new JwtTokenAdminInterceptor());
  }
}
