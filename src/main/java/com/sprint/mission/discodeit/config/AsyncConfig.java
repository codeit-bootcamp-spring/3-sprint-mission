package com.sprint.mission.discodeit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

    private static final String CONFIG_NAME = "[AsyncConfig] ";

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 4;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive, String prefix) {

        if (core <= 0 || max <= 0 || queue < 0 || keepAlive < 0) {
            throw new IllegalArgumentException("ThreadPool 설정값은 양수여야 합니다.");
        }
        if (core > max) {
            throw new IllegalArgumentException("Core Pool Size는 Max Pool Size보다 클 수 없습니다.");
        }

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(prefix + "-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return mainTaskExecutor(4, 8, 200, 60);
    }

    @Bean(name = "mainTaskExecutor")
    public ThreadPoolTaskExecutor mainTaskExecutor(
            @Value("${async.executors.main.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.main.max-size:" + DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.main.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.main.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "main-exec");
    }

    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(
            @Value("${async.executors.file.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.file.max-size:" + DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.file.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.file.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "file-exec");
    }

    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor(
            @Value("${async.executors.notification.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.notification.max-size:" +  DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.notification.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.notification.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
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
