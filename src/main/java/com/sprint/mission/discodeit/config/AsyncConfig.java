package com.sprint.mission.discodeit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final String CONFIG_NAME = "[AsyncConfig] ";

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
        executor.initialize();

        return executor;
    }

    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(
            @Value("${async.executors.file.core-size:4}") int core,
            @Value("${async.executors.file.max-size:8}") int max,
            @Value("${async.executors.file.queue-capacity:100}") int queue,
            @Value("${async.executors.file.keep-alive-seconds:60}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "file-exec");
    }

    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor(
            @Value("${async.executors.notification.core-size:2}") int core,
            @Value("${async.executors.notification.max-size:4}") int max,
            @Value("${async.executors.notification.queue-capacity:500}") int queue,
            @Value("${async.executors.notification.keep-alive-seconds:60}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "notify-exec");
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new LoggingAsyncUncaughtExceptionHandler();
    }

    private static class LoggingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {

            Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
            String methodName = method.getName();

            logger.error(CONFIG_NAME + "method={}, exType={}, message={}", methodName, ex.getClass().getName(), ex.getMessage(), ex);

            if (params != null && params.length > 0) {
                try {
                    logger.error(CONFIG_NAME + "params={}", Arrays.toString(params));
                } catch (Exception ignore) {
                    // 일반적으로 로깅 중 발생한 예외는 무시하여 원본 오류 유지
                }
            }
        }
    }
}
