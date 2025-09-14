package com.sprint.mission.discodeit.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableAsync
public class AsyncConfig {

    // 공통 빌더
    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive,
        String prefix, TaskDecorator decorator) {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(core);
        exec.setMaxPoolSize(max);
        exec.setQueueCapacity(queue);
        exec.setKeepAliveSeconds(keepAlive);
        exec.setThreadNamePrefix(prefix + "-");
        exec.setTaskDecorator(decorator);
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setAwaitTerminationSeconds(20);
        exec.initialize();
        return exec;
    }

    @Bean
    public TaskDecorator mdcSecurityContextTaskDecorator() {
        return result -> {
            // 현재 스레드의 MDC와 인증 정보
            Map<String, String> mdcMap = MDC.getCopyOfContextMap();
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    if (mdcMap != null) {
                        MDC.setContextMap(mdcMap);
                    } else {
                        MDC.clear();
                    }
                    SecurityContextHolder.setContext(securityContext);

                    result.run();

                } finally {
                    MDC.clear();
                    SecurityContextHolder.clearContext();
                }
            };
        };
    }

    // 기본 executor
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor(
        @Value("${discodeit.async.executors.default.core-size:4}") int core,
        @Value("${discodeit.async.executors.default.max-size:8}") int max,
        @Value("${discodeit.async.executors.default.queue-capacity:100}") int queue,
        @Value("${discodeit.async.executors.default.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "task-exec", mdcSecurityContextTaskDecorator);
    }

    // 알림(이메일/SMS) 발송 비동기 executor
    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor(
        @Value("${discodeit.async.executors.notification.core-size:4}") int core,
        @Value("${discodeit.async.executors.notification.max-size:8}") int max,
        @Value("${discodeit.async.executors.notification.queue-capacity:100}") int queue,
        @Value("${discodeit.async.executors.notification.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "notify-exec", mdcSecurityContextTaskDecorator);
    }

    // 파일 업로드/이미지 처리 등 executor
    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(
        @Value("${discodeit.async.executors.file.core-size:4}") int core,
        @Value("${discodeit.async.executors.file.max-size:8}") int max,
        @Value("${discodeit.async.executors.file.queue-capacity:100}") int queue,
        @Value("${discodeit.async.executors.file.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "file-exec", mdcSecurityContextTaskDecorator);
    }

    // 감사 로그/모니터링 등 백그라운드 executor
    @Bean(name = "auditTaskExecutor")
    public ThreadPoolTaskExecutor auditTaskExecutor(
        @Value("${discodeit.async.executors.audit.core-size:4}") int core,
        @Value("${discodeit.async.executors.audit.max-size:8}") int max,
        @Value("${discodeit.async.executors.audit.queue-capacity:100}") int queue,
        @Value("${discodeit.async.executors.audit.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "audit-exec", mdcSecurityContextTaskDecorator);
    }

    // 사용자 이벤트 비동기 executor
    @Bean(name = "userTaskExecutor")
    public ThreadPoolTaskExecutor userTaskExecutor(
        @Value("${discodeit.async.executors.user.core-size:4}") int core,
        @Value("${discodeit.async.executors.user.max-size:8}") int max,
        @Value("${discodeit.async.executors.user.queue-capacity:200}") int queue,
        @Value("${discodeit.async.executors.user.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "user-exec", mdcSecurityContextTaskDecorator);
    }

    // 채널 이벤트 비동기 executor
    @Bean(name = "channelTaskExecutor")
    public ThreadPoolTaskExecutor channelTaskExecutor(
        @Value("${discodeit.async.executors.channel.core-size:2}") int core,
        @Value("${discodeit.async.executors.channel.max-size:4}") int max,
        @Value("${discodeit.async.executors.channel.queue-capacity:100}") int queue,
        @Value("${discodeit.async.executors.channel.keep-alive-seconds:60}") int keepAlive,
        TaskDecorator mdcSecurityContextTaskDecorator
    ) {
        return buildExecutor(core, max, queue, keepAlive, "channel-exec", mdcSecurityContextTaskDecorator);
    }
}
