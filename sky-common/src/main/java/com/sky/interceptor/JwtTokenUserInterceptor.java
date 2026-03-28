package com.sky.interceptor;

import com.github.xiaoymin.knife4j.core.util.StrUtil;

import com.sky.context.BaseContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 1.获取请求头中的用户信息
        String userInfo = request.getHeader("userInfo");
        // 2.判断是否为空
        if (StrUtil.isNotBlank(userInfo)) {
            // 不为空，保存到ThreadLocal
            BaseContext.setCurrentId(Long.valueOf(userInfo));
        }
        // 3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 移除用户
        BaseContext.removeCurrentId();
    }
}
