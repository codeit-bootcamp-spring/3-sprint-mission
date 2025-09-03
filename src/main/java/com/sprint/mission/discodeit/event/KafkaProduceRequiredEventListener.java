package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Async("taskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
            log.info("[Kafka] MessageCreatedEvent 전송 완료: {}", payload);
        } catch (Exception e) {
            log.error("[Kafka] MessageCreateEvent 직렬화 실패", e);
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
        } catch (Exception e) {
            log.error("[Kafka] RoleUpdateEvent 직렬화 실패", e);
        }
    }

    @Async("taskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
        } catch (Exception e) {
            log.error("[Kafka] S3UploadFailedEvent 직렬화 실패", e);
        }
    }

}
