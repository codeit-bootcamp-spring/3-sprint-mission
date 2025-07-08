package com.sprint.mission.discodeit.config;

import java.util.Arrays;
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

    /**
     * 컨트롤러 계층의 하위 패키지 포함 모든 메서드에 대한 포인트 컷
     */
    @Pointcut("execution(* com.sprint.mission.discodeit.controller..*.*(..))")
    private void controllerLayer() {

    }

    /**
     * 서비스 계층의 하위 패키지 포함 모든 메서드에 대한 포인트 컷
     */
    @Pointcut("execution(* com.sprint.mission.discodeit.service..*.*(..))")
    private void serviceLayer() {

    }

    @Around("controllerLayer()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Controller");
    }

    @Around("serviceLayer()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecution(joinPoint, "Service");
    }

    /**
     * 공통 로깅 로직 수행하는 메서드 메서드 실행 전후로 로그 출력
     *
     * @param joinPoint 실행 대상 메서드에 대한 정보
     * @param layer     "Controller", "Service" 구분용 문자열
     * @return 실행 결과 (원래 메서드의 반환값)
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외
     */
    private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.info("[{}] {}#{} 시작 - args={}", layer, className, methodName,
            Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            log.info("[{}] {}#{} 완료 - {}ms - result={}", layer, className, methodName, elapsed,
                result != null ? result.getClass().getSimpleName() : null);

            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("[{}] {}#{} 예외 발생 - {}ms - {}: {}", layer, className, methodName, elapsed,
                t.getClass().getSimpleName(), t.getMessage());

            throw t;
        }
    }
}
