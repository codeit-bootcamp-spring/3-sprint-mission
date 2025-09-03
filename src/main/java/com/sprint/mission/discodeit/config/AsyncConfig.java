package com.sprint.mission.discodeit.config;

import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
@EnableRetry
@RequiredArgsConstructor
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-");
        executor.setTaskDecorator(taskDecorator());
        executor.initialize();
        return executor;
    }

    @Bean("eventTaskExecutor")
    public TaskExecutor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }

    @Bean
    public TaskDecorator taskDecorator() {
        return new ContextTaskDecorator();
    }

    private static class ContextTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 현재 스레드의 MDC와 SecurityContext 복사
            String requestId = MDC.get("requestId");
            String userId = MDC.get("userId");

            org.springframework.security.core.context.SecurityContext securityContext =
                SecurityContextHolder.getContext();
            Authentication authentication = securityContext.getAuthentication();

            return () -> {
                try {
                    // 비동기 스레드에서 컨텍스트 복원
                    if (requestId != null) {
                        MDC.put("requestId", requestId);
                    }
                    if (userId != null) {
                        MDC.put("userId", userId);
                    }
                    if (authentication != null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                    // 원본 작업 실행
                    runnable.run();
                } finally {
                    // 스레드 완료 후 컨텍스트 정리
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        }
    }
}