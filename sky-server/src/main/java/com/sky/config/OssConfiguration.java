package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {
  @Bean
  @ConditionalOnMissingBean
  /**
   * 作用: 执行ossUtil相关逻辑。
   * 输入: AliOssProperties aliOssProperties。
   * 输出: AliOssUtil。
   */
  public AliOssUtil ossUtil(AliOssProperties aliOssProperties) {
    log.info("创建AliOssUtil对象...");
    return new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(),
        aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
  }

}
