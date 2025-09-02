package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.mdc.decorator.MDCTaskDecorator;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig implements AsyncConfigurer {

    /**
     * ThreadPoolTaskExecutor 공통 빌더
     *
     * @param core      항상 유지되는 최소 스레드 수
     * @param max       최대 스레드 수
     * @param queue     작업을 대기시킬 큐의 크기 -> corePoolSize 만큼 스레드가 모두 사용 중일 때 새로운 작업들이 대기
     * @param keepAlive 유휴 스레드의 생존 시간
     * @param prefix    생성되는 스레드의 이름 접두사
     * @return ThreadPoolTaskExecutor 객체
     */
    private ThreadPoolTaskExecutor buildExecutor(int core, int max, int queue, int keepAlive,
        String prefix) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(core);
        executor.setMaxPoolSize(max);
        executor.setQueueCapacity(queue);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setThreadNamePrefix(prefix + "-");
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setTaskDecorator(new MDCTaskDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);

        executor.initialize();

        return executor;
    }

    @Bean(name = "binaryContentExecutor")
    public ThreadPoolTaskExecutor binaryContentExecutor(
        @Value("${async.executors.binary-content.core-size}") int core,
        @Value("${async.executors.binary-content.max-size}") int max,
        @Value("${async.executors.binary-content.queue-capacity}") int queue,
        @Value("${async.executors.binary-content.keep-alive}") int keepAlive) {

        return buildExecutor(core, max, queue, keepAlive, "binaryContent-exec");
    }

    @Bean(name = "notificationExecutor")
    public ThreadPoolTaskExecutor notificationExecutor(
        @Value("${async.executors.notification.core-size}") int core,
        @Value("${async.executors.notification.max-size}") int max,
        @Value("${async.executors.notification.queue-capacity}") int queue,
        @Value("${async.executors.notification.keep-alive}") int keepAlive) {

        return buildExecutor(core, max, queue, keepAlive, "binaryContent-exec");
    }
}
