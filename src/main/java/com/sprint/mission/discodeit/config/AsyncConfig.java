package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    SecurityContextHolder.setContext(securityContext);

                    runnable.run();
                } finally {
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }

    private ThreadPoolTaskExecutor buildExecutor(
        int core, int max, int queue, int keepAlive, String prefix, TaskDecorator decorator
    ) {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();

        exec.setCorePoolSize(core);
        exec.setMaxPoolSize(max);
        exec.setQueueCapacity(queue);
        exec.setKeepAliveSeconds(keepAlive);
        exec.setThreadNamePrefix(prefix + "-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setAwaitTerminationSeconds(20);
        exec.setTaskDecorator(decorator);
        exec.initialize();

        return exec;
    }

    @Bean(name = "eventTaskExecutor")
    public ThreadPoolTaskExecutor eventTaskExecutor(TaskDecorator mdcTaskDecorator) {
        return buildExecutor(2, 4, 100, 60, "event-exec", mdcTaskDecorator);
    }

    @Bean(name = "binaryContentTaskExecutor")
    public ThreadPoolTaskExecutor binaryContentTaskExecutor(TaskDecorator mdcTaskDecorator) {
        return buildExecutor(4, 8, 100, 120, "binarycontent-exec", mdcTaskDecorator);
    }

    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor(TaskDecorator mdcTaskDecorator) {
        return buildExecutor(4, 8, 400, 60, "notification-exec", mdcTaskDecorator);
    }
}