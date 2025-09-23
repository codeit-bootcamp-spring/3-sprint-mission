package com.sprint.mission.discodeit.event.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryStorageFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
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

    public static final String TOPIC_MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
    public static final String TOPIC_ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
    public static final String TOPIC_BINARY_STORAGE_FAILED = "discodeit.BinaryStorageFailedEvent";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("asyncExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        send(TOPIC_MESSAGE_CREATED, event);
    }

    @Async("asyncExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        send(TOPIC_ROLE_UPDATED, event);
    }

    @Async("asyncExecutor")
    @EventListener
    public void on(BinaryStorageFailedEvent event) {
        send(TOPIC_BINARY_STORAGE_FAILED, event);
    }

    private void send(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info("[Kafka] produced topic={}, payload={}", topic, payload);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] serialization failed. topic={}, event={}", topic, event, e);
        }
    }
}
