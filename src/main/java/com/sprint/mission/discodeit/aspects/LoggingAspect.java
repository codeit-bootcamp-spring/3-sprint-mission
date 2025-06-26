package com.sprint.mission.discodeit.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around(
      "execution(* com.sprint.mission.discodeit.controller.*.*(..)) || " +
          "execution(* com.sprint.mission.discodeit.service.*.*(..)) || " +
          "execution(* com.sprint.mission.discodeit.repository.*.*(..))"
  )
  public Object setLog(ProceedingJoinPoint joinPoint) throws Throwable {
    String prefix = "com.sprint.mission.discodeit.";
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String simplifiedName =
        className.startsWith(prefix) ? className.substring(prefix.length()) : className;
    String methodName = joinPoint.getSignature().getName();
    /* joinpoint의 매개변수 출력(단, 타겟 메서드의 매개변수가 하나 이상일 때)  */
    String params = "";
    if (joinPoint.getArgs().length > 0) {
      params += " " + joinPoint.getArgs()[0];
    }

    log.info("▶▶▶Entering {}.{}({})", simplifiedName, methodName, params);
    try {
      Object result = joinPoint.proceed(); // 실제 메서드 실행
      log.info("▶▶▶Exiting {}.{}({})", simplifiedName, methodName, params);
      return result;
    } catch (Throwable e) {
      log.error("▶▶▶Exception {}.{}({}) : {}", simplifiedName, methodName, params, e.getClass(), e);
      throw e;
    }
  }

}
