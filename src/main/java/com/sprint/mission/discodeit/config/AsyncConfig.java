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

/**
 * 비동기 작업을 위한 스레드 풀 설정을 담당하는 클래스입니다.
 * 
 * <p>메인 작업, 파일 처리, 알림 전송을 위한 별도의 스레드 풀을 구성하여
 * 각 작업 유형별로 최적화된 비동기 처리를 지원합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>메인 작업용 스레드 풀 (mainTaskExecutor)</li>
 *   <li>파일 처리용 스레드 풀 (fileTaskExecutor)</li>
 *   <li>알림 전송용 스레드 풀 (notificationTaskExecutor)</li>
 *   <li>비동기 예외 처리 및 로깅</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

    private static final String CONFIG_NAME = "[AsyncConfig] ";

    private static final int DEFAULT_CORE_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_POOL_SIZE = 4;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    /**
     * 스레드 풀 실행자를 생성하는 공통 메소드입니다.
     * 
     * @param core 코어 스레드 수
     * @param max 최대 스레드 수
     * @param queue 큐 용량
     * @param keepAlive 유휴 스레드 유지 시간 (초)
     * @param prefix 스레드 이름 접두사
     * @return 구성된 ThreadPoolTaskExecutor
     * @throws IllegalArgumentException 잘못된 설정값이 제공된 경우
     */
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

    /**
     * 기본 비동기 실행자를 반환합니다.
     * 
     * @return 메인 작업용 스레드 풀 실행자
     */
    @Override
    public Executor getAsyncExecutor() {
        return mainTaskExecutor(4, 8, 200, 60);
    }

    /**
     * 메인 작업을 위한 스레드 풀 실행자를 생성합니다.
     * 
     * @param core 코어 스레드 수 (기본값: 2)
     * @param max 최대 스레드 수 (기본값: 4)
     * @param queue 큐 용량 (기본값: 100)
     * @param keepAlive 유휴 스레드 유지 시간 (기본값: 60초)
     * @return 메인 작업용 ThreadPoolTaskExecutor
     */
    @Bean(name = "mainTaskExecutor")
    public ThreadPoolTaskExecutor mainTaskExecutor(
            @Value("${async.executors.main.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.main.max-size:" + DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.main.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.main.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "main-exec");
    }

    /**
     * 파일 처리를 위한 스레드 풀 실행자를 생성합니다.
     * 
     * @param core 코어 스레드 수 (기본값: 2)
     * @param max 최대 스레드 수 (기본값: 4)
     * @param queue 큐 용량 (기본값: 100)
     * @param keepAlive 유휴 스레드 유지 시간 (기본값: 60초)
     * @return 파일 처리용 ThreadPoolTaskExecutor
     */
    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileTaskExecutor(
            @Value("${async.executors.file.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.file.max-size:" + DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.file.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.file.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "file-exec");
    }

    /**
     * 알림 전송을 위한 스레드 풀 실행자를 생성합니다.
     * 
     * @param core 코어 스레드 수 (기본값: 2)
     * @param max 최대 스레드 수 (기본값: 4)
     * @param queue 큐 용량 (기본값: 100)
     * @param keepAlive 유휴 스레드 유지 시간 (기본값: 60초)
     * @return 알림 전송용 ThreadPoolTaskExecutor
     */
    @Bean(name = "eventTaskListener")
    public ThreadPoolTaskExecutor notificationTaskExecutor(
            @Value("${async.executors.notification.core-size:" + DEFAULT_CORE_POOL_SIZE + "}") int core,
            @Value("${async.executors.notification.max-size:" +  DEFAULT_MAX_POOL_SIZE + "}") int max,
            @Value("${async.executors.notification.queue-capacity:" + DEFAULT_QUEUE_CAPACITY + "}") int queue,
            @Value("${async.executors.notification.keep-alive-seconds:" + DEFAULT_KEEP_ALIVE_SECONDS + "}") int keepAlive
    ) {
        return buildExecutor(core, max, queue, keepAlive, "notify-exec");
    }

    /**
     * 비동기 작업에서 발생한 예외를 처리하는 핸들러를 반환합니다.
     * 
     * @return 로깅 기반 예외 처리 핸들러
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new LoggingAsyncUncaughtExceptionHandler();
    }

    /**
     * 비동기 작업에서 발생한 예외를 로깅하는 내부 클래스입니다.
     */
    private static class LoggingAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

        /**
         * 처리되지 않은 비동기 예외를 로깅합니다.
         * 
         * @param ex 발생한 예외
         * @param method 예외가 발생한 메소드
         * @param params 메소드 호출 시 전달된 매개변수
         */
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
