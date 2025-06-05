package com.sprint.mission.discodeit.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut: @RestController 또는 @Controller 클래스 내의 모든 메서드
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("---->> Controller 호출: {}", method);
        if (args.length > 0) {
            for (Object arg : args) {
                log.info("인자: {}", arg);
            }
        }
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("----[V] {} 종료, 반환값: {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logError(JoinPoint joinPoint, Throwable e) {
        log.error("----[X] {} 예외 발생: {}", joinPoint.getSignature().toShortString(), e.getMessage(), e);
    }
}