package com.sprint.mission.discodeit.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 컨트롤러 계층의 모든 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* com.sprint.mission.discodeit.controller..*.*(..))")
    private void controllerLayer() {
    }

    /**
     * 서비스 계층의 모든 메서드에 대한 포인트컷
     */
    @Pointcut("execution(* com.sprint.mission.discodeit.service..*.*(..))")
    private void serviceLayer() {
    }

    /**
     * 컨트롤러 레이어 로깅
     */
    @Around("controllerLayer()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Controller");
    }

    /**
     * 서비스 레이어 로깅
     */
    @Around("serviceLayer()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Service");
    }

    /**
     * 공통 실행 및 로깅 처리
     */
    private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.info("[{}] {}#{} 시작 - args={}", layer, className, methodName,
            Arrays.toString(joinPoint.getArgs()));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.info("[{}] {}#{} 완료 - {}ms - result={}", layer, className, methodName, elapsed,
                result != null ? result.getClass().getSimpleName() : "null");

            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;

            log.error("[{}] {}#{} 예외 발생 - {}ms - {}: {}", layer, className, methodName, elapsed,
                t.getClass().getSimpleName(), t.getMessage());

            throw t;
        }
    }
}
