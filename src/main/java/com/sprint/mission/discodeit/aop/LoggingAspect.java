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

    @Pointcut("execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.create*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.update*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicUserService.delete*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.create*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.update*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicChannelService.delete*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.create*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.update*(..)) || " +
            "execution(* com.sprint.mission.discodeit.service.basic.BasicMessageService.delete*(..)) || " +
            "execution(* com.sprint.mission.discodeit.storage.LocalBinaryContentStorage.put*(..)) || " +
            "execution(* com.sprint.mission.discodeit.storage.LocalBinaryContentStorage.download*(..))")
    public void businessServicePointcut() {
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

}
