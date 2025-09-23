package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.Executor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public TaskDecorator mdcSecurityContextTaskDecorator() {
        return runnable -> {
            final Map<String, String> parentMdc = MDC.getCopyOfContextMap();
            final Authentication parentAuthentication =
                SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : null;

            return () -> {
                final Map<String, String> previousMdc = MDC.getCopyOfContextMap();
                final SecurityContext previousSecCtx = SecurityContextHolder.getContext();

                try {
                    if (parentMdc != null) {
                        MDC.setContextMap(parentMdc);
                    } else {
                        MDC.clear();
                    }

                    if (parentAuthentication != null) {
                        SecurityContext child = SecurityContextHolder.createEmptyContext();
                        child.setAuthentication(parentAuthentication);
                        SecurityContextHolder.setContext(child);
                    } else {
                        SecurityContextHolder.clearContext();
                    }

                    runnable.run();
                } finally {

                    if (previousMdc != null) {
                        MDC.setContextMap(previousMdc);
                    } else {
                        MDC.clear();
                    }

                    if (previousSecCtx != null) {
                        SecurityContextHolder.setContext(previousSecCtx);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                }
            };
        };
    }

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor(TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("Async-");
        executor.setTaskDecorator(taskDecorator);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
