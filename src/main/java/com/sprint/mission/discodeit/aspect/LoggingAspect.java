package com.sprint.mission.discodeit.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.*(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.*(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.*(..))")
    public void serviceLayer() {
    }

    @Pointcut("execution(* com.sprint.mission.discodeit.controller.UserController.*(..))" +
            "|| execution(* com.sprint.mission.discodeit.controller.ChannelController.*.*(..))" +
            "|| execution(* com.sprint.mission.discodeit.controller.MessageController.*(..))")
    public void controllerLayer() {
    }

    /**
     * 메서드 실행 전에 로그를 기록한다.
     *
     * @param joinPoint 조인포인트 정보
     */
    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("{}.{} 실행 시작 | 매개변수: {}",
                className, methodName, Arrays.toString(args));
    }

    /**
     * 메서드 정상 실행 후에 로그를 기록한다.
     *
     * @param joinPoint 조인포인트 정보
     * @param result    메서드 반환값
     */
    @AfterReturning(pointcut = "serviceLayer() || controllerLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("{}.{} 실행 완료 | 반환값: {}",
                className, methodName, result);
    }

    /**
     * 메서드 실행 중 예외 발생 시 로그를 기록한다.
     *
     * @param joinPoint 조인포인트 정보
     * @param exception 발생한 예외
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("!!! {}.{} 실행 중 예외 발생 - 예외: {}, 메시지: {}",
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    /**
     * 메서드 실행 시간을 측정하고 로그를 기록한다.
     *
     * @param proceedingJoinPoint 프로시딩 조인포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = proceedingJoinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.info("{}.{} 실행 시간: {}ms", className, methodName, executionTime);

            // 성능 경고 (1초 이상 소요 시)
            if (executionTime > 1000) {
                log.warn("[s2][LoggingAspect] {}.{} 실행 시간이 {}ms로 느립니다. 성능 최적화가 필요합니다.",
                        className, methodName, executionTime);
            }

            return result;

        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            log.error("{}.{} 실행 실패 | 실행 시간: {}ms, 예외: {}",
                    className, methodName, executionTime, throwable.getMessage());

            throw throwable;
        }
    }

    /**
     * 비즈니스 이벤트 로깅을 위한 포인트컷
     * 사용자, 채널, 메시지 생성/수정/삭제, 파일 업로드/다운로드
     */
    @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.create(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.update(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.deleteById(..))" +

            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.createPublicChannel(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.createPrivateChannel(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.update(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.deleteById(..))" +

            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.create(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.updateContent(..))" +
            "|| execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.deleteById(..))" +

            "|| execution(* com.sprint.mission.discodeit.storage.LocalBinaryContentStorage.put(..))" +
            "|| execution(* com.sprint.mission.discodeit.storage.LocalBinaryContentStorage.download(..))")
    public void businessEvents() {
    }

    /**
     * 중요한 비즈니스 이벤트에 대한 상세 로깅을 수행한다.
     *
     * @param proceedingJoinPoint 프로시딩 조인포인트
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     */
    @Around("businessEvents()")
    public Object logBusinessEvent(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        Object[] args = proceedingJoinPoint.getArgs();

        log.info("비즈니스 이벤트 시작 - 작업: {}, 매개변수: {}", methodName, Arrays.toString(args));

        try {
            Object result = proceedingJoinPoint.proceed();

            log.info("비즈니스 이벤트 성공 - 작업: {}, 결과: {}", methodName, result);

            return result;

        } catch (Throwable throwable) {
            log.error("비즈니스 이벤트 실패 - 작업: {}, 예외: {}, 메시지: {}",
                    methodName, throwable.getClass().getSimpleName(), throwable.getMessage());

            throw throwable;
        }
    }
}
