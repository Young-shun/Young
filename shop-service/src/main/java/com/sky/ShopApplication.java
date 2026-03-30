package com.sky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import com.sky.api.config.ApiCommonWebMvcConfig;

@SpringBootApplication(scanBasePackages = "com.sky")
@MapperScan("com.sky.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sky.api.client")
@ComponentScan(basePackages = "com.sky")
@Import(ApiCommonWebMvcConfig.class)
public class ShopApplication {
    /**
     * 作用: 执行main相关逻辑。
     * 输入: String[] args。
     * 输出: 无。
     */
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
