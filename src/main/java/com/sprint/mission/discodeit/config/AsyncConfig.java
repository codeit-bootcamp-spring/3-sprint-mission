package com.sprint.mission.discodeit.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    public TaskDecorator contextPropagatingTaskDecorator() {
        return runnable -> {
            Map<String, String> parentMdc = MDC.getCopyOfContextMap();
            SecurityContext parentContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    if (parentMdc != null) {
                        MDC.setContextMap(parentMdc);
                    } else {
                        MDC.clear();
                    }
                    SecurityContextHolder.setContext(parentContext);

                    runnable.run();
                } finally {
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }

    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive,
        String prefix, TaskDecorator decorator) {

        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();

        // 스레드 풀의 세부 스펙을 설정한다.
        exec.setCorePoolSize(core);
        exec.setMaxPoolSize(max);
        exec.setQueueCapacity(queue);
        exec.setKeepAliveSeconds(keepAlive);
        exec.setThreadNamePrefix(prefix + "-");
        // 스레드 풀이 포화일 때 호출 스레드에서 작업을 실행하여 백프레셔(완만한 저하)를 유도한다.
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 애플리케이션 종료 시 진행 중 작업을 최대한 정리한다.
        exec.setWaitForTasksToCompleteOnShutdown(true);
        // 애플리케이션 종료 시 진행 중 작업을 최대한 정리한다.
        exec.setAwaitTerminationSeconds(20);
        // exec.setTaskDecorator(new MdcTaskDecorator()); // 예: 로깅 MDC/보안 컨텍스트 전파

        if (decorator != null) exec.setTaskDecorator(decorator);

        // 스레드 풀을 초기화한다.
        exec.initialize();

        // 설정된 스레드 풀을 반환한다.
        return exec;
    }

    @Bean(name = "appTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        return buildExecutor(4, 16, 500, 60, "async", contextPropagatingTaskDecorator());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new LoggingAsyncUncaughtExceptionHandler();
    }

    private static class LoggingAsyncUncaughtExceptionHandler implements
        AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {

            Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
            String methodName = method.getName();
            logger.error("[AsyncUncaughtException] method={} exType={} message={}", methodName,
                ex.getClass().getName(), ex.getMessage(), ex);

            if (params != null && params.length > 0) {
                try {
                    logger.error("[AsyncUncaughtException] params={}",
                        java.util.Arrays.toString(params));
                } catch (Exception ignore) {
                }
            }
        }
    }
}
