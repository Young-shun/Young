package com.sky.notation.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import com.sky.notation.AutoFill;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
  /**
   * 切入点
   */
  @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.notation.AutoFill)")
  public void autoFillPointCut() {
  }

  /**
   * 前置通知
   * 
   * @param joinPoint
   */
  @Before("autoFillPointCut()")
  public void beforeAutoFill(JoinPoint joinPoint) {
    log.info("Before auto fill: {}", joinPoint.getSignature());
    // 获取被拦截的数据库操作类型
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
    OperationType operationType = autoFill.value();
    // 获取实体对象
    Object[] args = joinPoint.getArgs();
    Object entity = args[0];
    // 准备填充数据
    LocalDateTime now = LocalDateTime.now();
    Long currentId = BaseContext.getCurrentId();
    switch (operationType) {
      case INSERT:
        // 填充创建人和创建时间
        try {
          Method setCreateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME,
              LocalDateTime.class);
          setCreateTimeMethod.invoke(entity, now);
          Method setCreateByMethod = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
          setCreateByMethod.invoke(entity, currentId);
          Method setUpdateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,
              LocalDateTime.class);
          setUpdateTimeMethod.invoke(entity, now);
          Method setUpdateByMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
          setUpdateByMethod.invoke(entity, currentId);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        break;
      case UPDATE:
        try {
          Method setUpdateTimeMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,
              LocalDateTime.class);
          setUpdateTimeMethod.invoke(entity, now);
          Method setUpdateByMethod = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
          setUpdateByMethod.invoke(entity, currentId);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      default:
        break;
    }
  }

}
