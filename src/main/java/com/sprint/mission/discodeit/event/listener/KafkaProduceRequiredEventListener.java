package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "discodeit.notification", name = "listener", havingValue = "kafka")
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        sendEvent("discodeit.MessageCreatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        sendEvent("discodeit.RoleUpdatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        sendEvent("discodeit.S3UploadFailedEvent", event);
    }

    private void sendEvent(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info("[KafkaProducer] 이벤트 발행 성공 - topic={}, payload={}", topic, payload);
        } catch (Exception e) {
            log.error("[KafkaProducer] 이벤트 발행 실패 - topic={}, event={}", topic, event, e);
        }
    }
}