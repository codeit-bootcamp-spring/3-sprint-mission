package com.sprint.mission.discodeit.aop;

import com.sprint.mission.discodeit.annotation.Logging;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Autowired
  private HttpServletRequest request;

  @Before("@annotation(logging)")
  public void logBefore(JoinPoint joinPoint, Logging logging) {
    log.info("ฅ(◑︿◐)ฅ ────❥ Executing {} with args: {}", joinPoint.getSignature(),
        joinPoint.getArgs());
  }

  @AfterReturning(pointcut = "@annotation(logging)", returning = "result")
  public void logAfter(JoinPoint joinPoint, Logging logging, Object result) {
    log.info("(๑^᎑^๑)っ────❥ Success: {} returned = {}", joinPoint.getSignature(), result);
  }

  @AfterThrowing(pointcut = "@annotation(logging)", throwing = "e")
  public void logException(JoinPoint joinPoint, Logging logging, Throwable e) {
    log.error("(;;´◎Д◎)!!◤◢◤◢ Exception in {}: {}", joinPoint.getSignature(),
        e.getMessage(), e);
  }

  @Before("@annotation(logging)")
  public void logRequestInfo(JoinPoint joinPoint, Logging logging) {
    log.info("ﾟ｡⋆\uD835\uDE43\uD835\uDE4F\uD835\uDE4F\uD835\uDE4B ⋆｡ﾟ☁\uFE0E {} {} - Method: {}",
        request.getRemoteAddr(), request.getRequestURI(),
        request.getMethod());
  }
}
