package com.sprint.mission.discodeit.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.sprint.mission.discodeit.service..*Service.*(..))")
    public void businessServicePointcut() {
    }

    // 페이지네이션 메서드 전용 포인트컷
    @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.findAllByChannelId(..))")
    public void paginationServicePointcut() {
    }

    @Around("businessServicePointcut()")
    public Object logBusinessEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("[{}] 비즈니스 이벤트 시작 - 작업: {}, 매개변수: {}", className, methodName, Arrays.toString(args));

        try {
            Object result = joinPoint.proceed();
            log.info("[{}] 비즈니스 이벤트 성공 - 작업: {}, 결과: {}", className, methodName, result);
            return result;
        } catch (Throwable throwable) {
            log.error("[{}] 비즈니스 이벤트 실패 - 작업: {}, 예외: {}, 메시지: {}",
                    className, methodName, throwable.getClass().getSimpleName(), throwable.getMessage());
            throw throwable;
        }
    }

    @Around("paginationServicePointcut()")
    public Object logPaginationEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String channelId = args.length > 0 ? String.valueOf(args[0]) : "N/A";
        String cursor = args.length > 1 ? String.valueOf(args[1]) : "N/A";
        String pageSize = (args.length > 2 && args[2] instanceof org.springframework.data.domain.Pageable)
                ? String.valueOf(((org.springframework.data.domain.Pageable) args[2]).getPageSize())
                : "N/A";

        log.info("[Pagination] 채널ID={}, 커서={}, 페이지사이즈={}", channelId, cursor, pageSize);

        try {
            Object result = joinPoint.proceed();
            log.info("[Pagination] 메시지 조회 성공");
            return result;
        } catch (Throwable throwable) {
            log.error("[Pagination] 메시지 조회 실패 - 예외: {}, 메시지: {}",
                    throwable.getClass().getSimpleName(), throwable.getMessage());
            throw throwable;
        }
    }

}
