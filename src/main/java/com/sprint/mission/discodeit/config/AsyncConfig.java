package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.util.MdcTaskDecorator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive, String prefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(prefix + "-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();

        return executor;
    }

    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(
            @Value("${async.executors.file.core-size:2}") int core,
            @Value("${async.executors.file.max-size:4}") int max,
            @Value("${async.executors.file.queue-capacity:50}") int queue,
            @Value("${async.executors.file.keep-alive-seconds:120}") int keepAlive)
    {
        return buildExecutor(core, max, queue, keepAlive, "file-exec");
    }

    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor(
            @Value("${async.executors.notification.core-size:2}") int core,
            @Value("${async.executors.notification.max-size:4}") int max,
            @Value("${async.executors.notification.queue-capacity:200}") int queue,
            @Value("${async.executors.notification.keep-alive-seconds:60}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "notify-exec");
    }
}
