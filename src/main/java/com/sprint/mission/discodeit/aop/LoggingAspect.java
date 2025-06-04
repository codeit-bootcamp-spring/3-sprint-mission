package com.sprint.mission.discodeit.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

//    @Pointcut("execution(* com.sprint.mission.discodeit.controller..*(..))")
//    public void controllerMethods() {}

    @Pointcut("execution(* com.sprint.mission.discodeit.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.sprint.mission.discodeit.repository..*(..))")
    public void repositoryMethods() {}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        log.info(">> Start: {}", method);
        log.info(">> Args: {}", Arrays.toString(args));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();
        log.info("V End: {}", method);
        log.info("V Returned: {}", result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        log.error("* Exception in {}: {}", method, ex.getMessage(), ex);
    }

//    @Before("repositoryMethods()")
//    public void logRepository(JoinPoint joinPoint) {
//        System.out.println(joinPoint.getSignature().getName());
//    }
}
