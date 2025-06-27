package com.sprint.mission.discodeit.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // 컨트롤러 메소드들을 대상으로 하는 포인트컷
    @Pointcut("execution(* com.sprint.mission.discodeit.controller..*(..))")
    public void controllerMethods() {
    }

    // 서비스 메소드들을 대상으로 하는 포인트컷
    @Pointcut("execution(* com.sprint.mission.discodeit.service..*(..))")
    public void serviceMethods() {
    }

    // CRUD 관련 메소드들을 대상으로 하는 포인트컷
    @Pointcut("execution(* *.create*(..)) || execution(* *.update*(..)) || execution(* *.delete*(..))")
    public void crudMethods() {
    }

    /**
     * 컨트롤러 메소드 실행 전후 로깅 (Around Advice)
     */
    @Around("controllerMethods()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 메소드 파라미터 로깅 (민감한 정보 제외)
        Object[] args = joinPoint.getArgs();
        String sanitizedArgs = sanitizeArguments(args);

        log.info("[{}::{}] 요청 시작 - 파라미터: {}", className, methodName, sanitizedArgs);

        stopWatch.start();
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            log.info("[{}::{}] 요청 완료 - 실행시간: {}ms",
                className, methodName, stopWatch.getTotalTimeMillis());

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("[{}::{}] 요청 실패 - 실행시간: {}ms, 오류: {}",
                className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 서비스 레이어의 CRUD 메소드 로깅 (Around Advice)
     */
    @Around("serviceMethods() && crudMethods()")
    public Object logServiceCrudExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        // 주요 식별자 추출 (UUID 등)
        String identifier = extractIdentifier(args);

        log.debug("[{}::{}] 서비스 메소드 시작 - 대상: {}", className, methodName, identifier);

        try {
            Object result = joinPoint.proceed();
            log.info("[{}::{}] {} 작업 성공 - 대상: {}",
                className, methodName, getOperationType(methodName), identifier);
            return result;
        } catch (IllegalArgumentException e) {
            log.warn("️ [{}::{}] {} 작업 실패 (잘못된 요청) - 대상: {}, 사유: {}",
                className, methodName, getOperationType(methodName), identifier, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(" [{}::{}] {} 작업 실패 (시스템 오류) - 대상: {}, 오류: {}",
                className, methodName, getOperationType(methodName), identifier, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 민감한 정보를 제거하고 파라미터를 문자열로 변환
     */
    private String sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "없음";
        }

        return Arrays.stream(args)
            .map(arg -> {
                if (arg == null) {
                    return "null";
                }

                String argStr = arg.toString();
                String className = arg.getClass().getSimpleName();

                // 민감한 정보가 포함된 클래스들은 클래스명만 표시
                if (className.toLowerCase().contains("password") ||
                    className.toLowerCase().contains("request") ||
                    className.toLowerCase().contains("multipart")) {
                    return className;
                }

                // UUID는 앞 8자리만 표시
                if (arg instanceof UUID) {
                    return argStr.substring(0, 8) + "...";
                }

                // 문자열이 너무 길면 자르기
                if (argStr.length() > 50) {
                    return argStr.substring(0, 50) + "...";
                }

                return argStr;
            })
            .reduce((a, b) -> a + ", " + b)
            .orElse("없음");
    }

    /**
     * 주요 식별자 추출 (UUID, ID 등)
     */
    private String extractIdentifier(Object[] args) {
        if (args == null || args.length == 0) {
            return "미지정";
        }

        for (Object arg : args) {
            if (arg instanceof UUID) {
                return "ID:" + arg.toString().substring(0, 8) + "...";
            }
            if (arg != null && arg.getClass().getSimpleName().toLowerCase().contains("request")) {
                // Request 객체에서 username이나 email 등을 추출할 수 있음
                return "Request:" + arg.getClass().getSimpleName();
            }
        }

        return "ID:미지정";
    }


    /**
     * 메소드명으로부터 작업 유형 추출
     */
    private String getOperationType(String methodName) {
        if (methodName.startsWith("create")) {
            return "생성";
        }
        if (methodName.startsWith("update")) {
            return "수정";
        }
        if (methodName.startsWith("delete")) {
            return "삭제";
        }
        if (methodName.startsWith("find")) {
            return "조회";
        }
        return "처리";
    }
}