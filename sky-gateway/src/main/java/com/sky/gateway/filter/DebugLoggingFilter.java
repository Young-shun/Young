package com.sky.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class DebugLoggingFilter implements GlobalFilter, Ordered {

  @Override
  /**
   * 作用: 执行filter相关逻辑。
   * 输入: ServerWebExchange exchange, GatewayFilterChain chain。
   * 输出: Mono<Void>。
   */
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    System.out.println("[GatewayDebug] " + request.getMethod() + " " + request.getURI());
    return chain.filter(exchange);
  }

  @Override
  /**
   * 作用: 执行getOrder相关逻辑。
   * 输入: 无。
   * 输出: int。
   */
  public int getOrder() {
    return -1000;
  }
}
