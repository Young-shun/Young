package com.sky.api.config;

import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 通用的 Web MVC 配置类
 * 用于在微服务中注册拦截器，确保用户信息从请求头中被正确提取和传播
 *
 * 使用方式：各个微服务可以导入此配置：@Import(ApiCommonWebMvcConfig.class)
 * 或者各个微服务创建自己的 WebMvcConfiguration 继承 WebMvcConfigurationSupport 并参考此类的实现
 */
@Configuration
@Slf4j
public class ApiCommonWebMvcConfig extends WebMvcConfigurationSupport {

  @Autowired
  private JwtTokenUserInterceptor jwtTokenUserInterceptor;

  /**
   * 注册拦截器
   * 用于从请求头的 "userInfo" 字段中读取用户ID，保存到 BaseContext（ThreadLocal）
   * 这样业务代码可以通过 BaseContext.getCurrentId() 获取当前用户ID
   * 同时 Feign 调用可以通过 FeignUserInfoConfig 的 RequestInterceptor 将用户ID转发到下游服务
   *
   * @param registry
   */
  protected void addInterceptors(InterceptorRegistry registry) {
    log.info("开始注册通用拦截器（API层）...");
    // 为所有 /user/** 和 /admin/** 路径添加拦截器，从请求头中读取用户信息
    registry.addInterceptor(jwtTokenUserInterceptor)
        .addPathPatterns("/user/**")
        .addPathPatterns("/admin/**")
        .addPathPatterns("/inner/**"); // 内部调用也支持用户信息传播
  }

  @Override
  /**
   * 作用: 执行extendMessageConverters相关逻辑。
   * 输入: List<HttpMessageConverter<?>> converters。
   * 输出: 无。
   */
  protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.info("开始注册通用消息转换器（API层）...");
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new JacksonObjectMapper());
    converters.add(0, converter);
  }
}
