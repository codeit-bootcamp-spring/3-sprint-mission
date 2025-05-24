package com.sprint.mission.discodeit.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * PackageName  : com.sprint.mission.discodeit.aop
 * FileName     : LoggingAspect
 * Author       : dounguk
 * Date         : 2025. 5. 25.
 * ===========================================================
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterThrowing(pointcut = "execution(* com.sprint.mission.discodeit..service..*(..))", throwing = "throwable")
    public void logException(JoinPoint joinPoint, Throwable throwable){
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        Object[] args = joinPoint.getArgs();
        log.error(className + ".\n" + methodName + "(" + Arrays.toString(args) + ")", throwable);
    }

}
