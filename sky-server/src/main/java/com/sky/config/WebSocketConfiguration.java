package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket配置类，用于注册WebSocket的Bean
 */
@Configuration
@Slf4j
// 核心：强制要求只有在 SERVLET 环境下才加载，网关是 REACTIVE 环境，会完美跳过
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSocketConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
