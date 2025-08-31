package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

    private static final String LISTENER_NAME = "[KafkaProduceRequiredEventListener] ";

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Async("eventTaskListener")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        try {
            log.info(LISTENER_NAME + "MessageCreatedEvent를 Kafka로 발행 시작 - messageId={}", event.message().getId());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);

            log.info(LISTENER_NAME + "MessageCreatedEvent Kafka 발행 완료 - messageId={}, topic: discodeit.MessageCreatedEvent",
                    event.message().getId());

        } catch (Exception e) {
            log.error(LISTENER_NAME + "MessageCreatedEvent Kafka 발행 실패 - messageId={}",
                    event.message().getId(), e);
        }
    }

    @Async("eventTaskListener")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        try {
            log.info(LISTENER_NAME + "RoleUpdatedEvent를 Kafka로 발행 시작 - userId={}", event.userId());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);

            log.info(LISTENER_NAME + "RoleUpdatedEvent Kafka 발행 완료 - userId: {}, topic: discodeit.RoleUpdatedEvent",
                    event.userId());

        } catch (Exception e) {
            log.error(LISTENER_NAME + "RoleUpdatedEvent Kafka 발행 실패 - userId: {}", event.userId(), e);
        }
    }

    @Async("eventTaskListener")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(S3UploadFailedEvent event) {
        try {
            log.info(LISTENER_NAME + "S3UploadFailedEvent를 Kafka로 발행 시작 - requestId: {}", event.requestId());

            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);

            log.info(LISTENER_NAME + "S3UploadFailedEvent Kafka 발행 완료 - requestId: {}, topic: discodeit.S3UploadFailedEvent",
                    event.requestId());

        } catch (Exception e) {
            log.error(LISTENER_NAME + "S3UploadFailedEvent Kafka 발행 실패 - requestId: {}", event.requestId(), e);
        }
    }
}
