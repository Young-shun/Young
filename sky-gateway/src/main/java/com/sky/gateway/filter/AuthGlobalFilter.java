package com.sky.gateway.filter;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import com.sky.gateway.Constant.JwtClaimsConstant;
import com.sky.gateway.properties.JwtProperties;
import com.sky.gateway.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {
  private final JwtProperties jwtProperties;

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // 1.获取Request
    ServerHttpRequest request = exchange.getRequest();
    // Diagnostic logging
    System.out.println("[AuthGlobalFilter] incoming request path: " + request.getURI());
    System.out.println("[AuthGlobalFilter] headers: " + request.getHeaders());
    // Only apply admin filter to /admin/** paths
    String rawPath = request.getPath().toString();
    if (!rawPath.startsWith("/admin/")) {
      return chain.filter(exchange);
    }
    // 判断当前拦截到的是Controller的方法还是其他资源
    if (isExclude(request.getPath().toString())) {
      // 无需拦截，直接放行
      return chain.filter(exchange);
    }
    // 3.获取请求头中的token
    String token = null;
    List<String> headers = request.getHeaders().get(jwtProperties.getAdminTokenName());
    if (headers != null && !headers.isEmpty()) {
      token = headers.get(0);
      System.out.println("[AuthGlobalFilter] token found in header: " + jwtProperties.getAdminTokenName());
    }
    if (token == null || token.trim().isEmpty()) {
      List<String> alt = request.getHeaders().get("token");
      if (alt != null && !alt.isEmpty()) {
        token = alt.get(0);
        System.out.println("[AuthGlobalFilter] token found in header: token");
      }
    }
    if (token == null || token.trim().isEmpty()) {
      String cookie = request.getHeaders().getFirst("Cookie");
      if (cookie != null) {
        for (String c : cookie.split(";")) {
          String[] kv = c.trim().split("=", 2);
          if (kv.length == 2 && "token".equals(kv[0])) {
            token = kv[1];
            System.out.println("[AuthGlobalFilter] token found in Cookie");
            break;
          }
        }
      }
    }
    // 4.校验并解析token
    Long empId = null;
    if (token == null || token.trim().isEmpty()) {
      // 缺失 token，返回 401
      ServerHttpResponse response = exchange.getResponse();
      response.setRawStatusCode(401);
      System.out.println("[AuthGlobalFilter] missing admin token");
      return response.setComplete();
    }
    try {
      Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
      empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
    } catch (Exception e) {
      // token 解析失败，返回 401
      ServerHttpResponse response = exchange.getResponse();
      response.setRawStatusCode(401);
      System.out.println("[AuthGlobalFilter] token parse failed: " + e.getMessage());
      return response.setComplete();
    }

    // 5.如果有效，传递用户信息
    System.out.println("empId = " + empId);
    System.out.println("[AuthGlobalFilter] token parsed successfully for empId=" + empId);
    String empInfo = empId.toString();
    ServerWebExchange context = exchange.mutate().request(b -> b.header("empInfo", empInfo)).build();
    // 6.放行
    return chain.filter(context);
  }

  private boolean isExclude(String antPath) {
    for (String pathPattern : jwtProperties.getExcludePaths()) {
      if (antPathMatcher.match(pathPattern, antPath)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
