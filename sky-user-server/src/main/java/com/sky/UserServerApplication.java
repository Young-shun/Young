package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Import;
import com.sky.api.config.ApiCommonWebMvcConfig;

@SpringBootApplication
@EnableTransactionManagement // 开启注解方式的事务管理
@Slf4j
@EnableScheduling
@Import(ApiCommonWebMvcConfig.class)
public class UserServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
        log.info("User server started");
    }
}
