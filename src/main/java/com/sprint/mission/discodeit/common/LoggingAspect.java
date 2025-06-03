package com.sprint.mission.discodeit.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Pointcut("execution(* com.sprint.mission.discodeit.controller..*.*(..))")
  private void controllerPointcut() {
  }

  @Around("controllerPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    long start = System.currentTimeMillis();
    Object result = null;
    try {
      result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - start;

      // 반환 값 요약 (길면 생략)
      String resultStr = result != null ? result.toString() : "null";
      if (resultStr.length() > 100) {
        resultStr = resultStr.substring(0, 97) + "...";
      }

      log.info("[{}#{}] executed in {}ms | args={} | return={}",
          className, methodName, executionTime,
          java.util.Arrays.toString(joinPoint.getArgs()), resultStr
      );

      return result;
    } catch (Exception e) {
      log.error("[{}#{}] exception={} | message={}",
          className, methodName, e.getClass().getSimpleName(), e.getMessage());
      throw e;
    }
  }
}
