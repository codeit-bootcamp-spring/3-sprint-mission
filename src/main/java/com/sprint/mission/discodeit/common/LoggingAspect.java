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

  private static final String START_LINE = "┌──────────────────────────────────────────────────────────────────────────────────";
  private static final String END_LINE = "└──────────────────────────────────────────────────────────────────────────────────";
  private static final String MID_LINE = "│                                                                                  ";

  // controller 패키지의 모든 클래스의 모든 메소드를 대상으로 합니다
  @Pointcut("execution(* com.sprint.mission.discodeit.controller..*.*(..))")
  private void controllerPointcut() {
  }

  // service 패키지의 모든 클래스의 모든 메소드를 대상으로 합니다
  @Pointcut("execution(* com.sprint.mission.discodeit.service..*.*(..))")
  private void servicePointcut() {
  }

  // controller와 service 모두에 적용합니다
  @Pointcut("controllerPointcut() || servicePointcut()")
  private void applicationPointcut() {
  }

  @Around("controllerPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();

    // 메소드 실행 전
    log.info(START_LINE);
    log.info("│ 메소드 실행: {}.{}()", className, methodName);

    // 메소드 파라미터 로깅
    Object[] args = joinPoint.getArgs();
    if (args != null && args.length > 0) {
      log.info("│ 파라미터:");
      for (int i = 0; i < args.length; i++) {
        Object arg = args[i];
        log.info("│ ├─ [{}]: {}", i, arg);
      }
    } else {
      log.info("│ 파라미터: 없음");
    }

    try {
      // 메소드 실행
      long start = System.currentTimeMillis();
      log.info("│ 메소드 실행 중...");
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - start;

      // 메소드 실행 결과 로깅
      log.info("│ 실행 시간: {}ms", executionTime);
      log.info("│ 메소드 종료: {}.{}()", className, methodName);

      // 메소드 반환 값 로깅
      if (result != null) {
        String resultStr = result.toString();
        if (resultStr.length() > 100) {
          // 긴 결과값은 줄임
          resultStr = resultStr.substring(0, 97) + "...";
        }
        log.info("│ 반환 값: {}", resultStr);
      } else {
        log.info("│ 반환 값: null");
      }

      log.info(END_LINE);
      return result;
    } catch (Exception e) {
      // 예외 발생 시 로깅
      log.error("│ 예외 발생: {}.{}()", className, methodName);
      log.error("│ 예외 종류: {}", e.getClass().getSimpleName());
      log.error("│ 예외 메시지: {}", e.getMessage());
      log.error(END_LINE);
      throw e;
    }
  }
}