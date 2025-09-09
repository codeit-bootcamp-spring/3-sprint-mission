package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.UserLogInOutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC_PREFIX = "discodeit.";


    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        send(event);
    }

    @Async("notificationExecutor")
    @CacheEvict(value = "notificationsByUser", key = "#event.user().id")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoleUpdated(RoleUpdatedEvent event) {
        send(event);
    }

    @Async("notificationExecutor")
    @EventListener
    public void onS3UploadFailed(S3UploadFailedEvent event) {
        send(event);
    }

    @Async("userLogInOutExecutor")
    @EventListener
    public void onUserLogInOut(UserLogInOutEvent event) {
        send(event);
    }

    private void send(Object event) {
        try {
            String topic = TOPIC_PREFIX + event.getClass().getSimpleName();
            String payload = objectMapper.writeValueAsString(event);

            log.debug("[KafkaProduceRequiredEventListener] 이벤트 발행 - topic: {} payload: {}",
                topic, payload);

            kafkaTemplate.send(topic, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
