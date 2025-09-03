package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreateEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreateEvent event) {
        try {
            log.info("[Kafka Producer]Kafka로 메시지 생성 이벤트 발급 - 스레드 : {}, 채널 {}", Thread.currentThread().getName(), event.channelId());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreateEvent", payload);

            log.info("[Kafka Producer]Kafka로 메시지 생성 이벤트 발급 완료 - 채널 : {}", event.channelId());
        } catch (Exception e) {
            log.error("[Kafka Producer]Kafka 메시지 생성 이벤트 발급 실패 - 채널 : {}, error : {}",
                event.channelId(), e.getMessage());
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        try {
            log.info("Kafka로 권한 변경 이벤트 발급 - 스레드 : {}, 사용자 : {} ", Thread.currentThread().getName(), event.userId());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);

            log.info("Kafka로 권한 변경 이벤트 발급 완료 - 사용자 : {}", event.userId());
        } catch (Exception e) {
            log.error("Kafka 권한 변경 이벤트 발급 실패 - 사용자 : {}, error : {}",
                event.userId(), e.getMessage());
        }
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        try {
            log.info("Kafka로 S3 업르도 실패 이벤트 발급 - 스레드 : {}", Thread.currentThread().getName());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);

            log.info("Kafka로 S3 업로드 실패 이벤트 발급 완료");
        } catch (Exception e) {
            log.error("Kafka S3 업로드 실패 이벤트 발급 실패 - error : {}", e.getMessage());
        }
    }
}
