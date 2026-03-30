package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Import;
import com.sky.api.config.ApiCommonWebMvcConfig;

@SpringBootApplication
@EnableTransactionManagement // 开启注解方式的事务管理
@Slf4j
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.sky.api.client")
@Import(ApiCommonWebMvcConfig.class)
public class ShoppingCartServerApplication {
    /**
     * 作用: 执行main相关逻辑。
     * 输入: String[] args。
     * 输出: 无。
     */
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartServerApplication.class, args);
        log.info("Shopping cart server started");
    }
}
