package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int keepAlive, String prefix){

        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();

        exec.setCorePoolSize(core);
        exec.setMaxPoolSize(max);
        exec.setKeepAliveSeconds(keepAlive);
        exec.setThreadNamePrefix(prefix + "-");


        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setAwaitTerminationSeconds(60);
        exec.setTaskDecorator(new MdcTaskDecorator());
        exec.initialize();

        return exec;
    }


    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor(
            @Value("${async.executors.core-size}") int core,
            @Value("${async.executors.max-size}") int max,
            @Value("${async.executors.queue-capacity}") int queue,
            @Value("${async.executors.keep-alive-seconds}") int keepAlive
    ) {
        return buildExecutor(core, max, keepAlive, "async-exec");
    }
}
