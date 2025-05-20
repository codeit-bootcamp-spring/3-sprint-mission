package com.sprint.mission.discodeit.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  @Before("execution(* com.sprint.mission.discodeit.controller.*.*(..))")
  public void controllerLogBefore(JoinPoint joinPoint) {
    String params = "";

    /* 필기. joinpoint의 매개변수 출력(단, 타겟 메서드의 매개변수가 하나 이상일 때)  */
    if (joinPoint.getArgs().length > 0) {
      params += " " + joinPoint.getArgs()[0];
//      System.out.println("Before joinPoint.getArgs()[0]: " + joinPoint.getArgs()[0]);
    }

    System.out.println(
        "👩 Controller execution: " + joinPoint.getTarget().getClass() + "  >> "
            + joinPoint.getSignature().getName() + " params : " + params);

  }

  @Before("execution(* com.sprint.mission.discodeit.service.*.*(..))")
  public void serviceLogBefore(JoinPoint joinPoint) {
    String params = "";

    /* 필기. joinpoint의 매개변수 출력(단, 타겟 메서드의 매개변수가 하나 이상일 때)  */
    if (joinPoint.getArgs().length > 0) {
      params += " " + joinPoint.getArgs()[0];
//      System.out.println("Before joinPoint.getArgs()[0]: " + joinPoint.getArgs()[0]);
    }

    System.out.println(
        "💱 Service execution: " + joinPoint.getTarget().getClass() + "  >> "
            + joinPoint.getSignature().getName() + " params : " + params);
  }

  @Before("execution(* com.sprint.mission.discodeit.repository.*.*(..))")
  public void repositoryLogBefore(JoinPoint joinPoint) {
    String params = "";

    /* 필기. joinpoint의 매개변수 출력(단, 타겟 메서드의 매개변수가 하나 이상일 때)  */
    if (joinPoint.getArgs().length > 0) {
      params += " " + joinPoint.getArgs()[0];
//      System.out.println("Before joinPoint.getArgs()[0]: " + joinPoint.getArgs()[0]);
    }

    System.out.println(
        "🗳 Repository execution: " + joinPoint.getTarget().getClass() + "  >> "
            + joinPoint.getSignature().getName() + " params : " + params);
  }
}
