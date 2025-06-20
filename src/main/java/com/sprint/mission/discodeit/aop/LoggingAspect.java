package com.sprint.mission.discodeit.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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

    // 0채널, 메세지, 유저의 CUD만 info로 로깅
    @Pointcut(
        "execution(* com.sprint.mission.discodeit.unit.basic.BasicChannelService.createChannel(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicChannelService.update(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicChannelService.deleteChannel(..))"
    )
    public void createUpdateDeleteChannelMethods() {}


    @Pointcut(
        "execution(* com.sprint.mission.discodeit.unit.basic.BasicMessageService.createMessage(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicMessageService.updateMessage(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicMessageService.deleteMessage(..))"
        )
    public void createUpdateDeleteMessageMethods() {}

    @Pointcut(
        "execution(* com.sprint.mission.discodeit.unit.basic.BasicUserService.create(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicUserService.deleteUser(..)) || "+
            "execution(* com.sprint.mission.discodeit.unit.basic.BasicUserService.update(..))"
    )
    public void createUpdateDeleteUserMethods() {}


    @Before("createUpdateDeleteChannelMethods() || createUpdateDeleteMessageMethods() || createUpdateDeleteUserMethods()")
    public void logStart(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Service method started " + className + ".\n" + methodName + "(" + Arrays.toString(args) + ")");
    }
}
