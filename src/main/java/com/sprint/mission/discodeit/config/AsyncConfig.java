package com.sprint.mission.discodeit.config;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 비동기 처리용 TaskExecutor app.async.enabled=true 일 때 사용
     */
    @Bean("taskExecutor")
    @ConditionalOnProperty(name = "app.async.enabled", havingValue = "true", matchIfMissing = true)
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * 동기 처리용 TaskExecutor app.async.enabled=false 일 때 사용 (현재 스레드에서 바로 실행)
     */
    @Bean("taskExecutor")
    @ConditionalOnProperty(name = "app.async.enabled", havingValue = "false")
    public Executor syncExecutor() {
        return Runnable::run;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    @Bean
    public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("[@Async ERROR] 비동기 실행 실패 - method={}, params={}, ex={}",
                method.getName(), Arrays.toString(params), ex.toString(), ex);
        };
    }

    /**
     * MDC와 SecurityContext를 비동기 스레드로 전파하는 데코레이터
     */
    static class ContextCopyingDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable task) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                Map<String, String> previousMdc = MDC.getCopyOfContextMap();
                SecurityContext previousSecurity = SecurityContextHolder.getContext();
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    } else {
                        MDC.clear();
                    }
                    SecurityContextHolder.setContext(securityContext);

                    task.run();
                } finally {
                    // 정리: 누수 방지
                    if (previousMdc != null) {
                        MDC.setContextMap(previousMdc);
                    } else {
                        MDC.clear();
                    }
                    SecurityContextHolder.setContext(previousSecurity);
                }
            };
        }
    }
}
