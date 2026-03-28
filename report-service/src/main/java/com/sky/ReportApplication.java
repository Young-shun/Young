package com.sky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import com.sky.api.config.ApiCommonWebMvcConfig;

@SpringBootApplication(scanBasePackages = "com.sky")
@MapperScan("com.sky.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sky.api.client")
@Import(ApiCommonWebMvcConfig.class)
public class ReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
