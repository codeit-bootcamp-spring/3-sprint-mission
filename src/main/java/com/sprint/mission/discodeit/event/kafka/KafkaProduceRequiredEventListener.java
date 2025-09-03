package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
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
    private static final String TOPIC_SUFFIX = "Event";
    private static final String MESSAGE_CREATED = TOPIC_PREFIX + "MessageCreated" + TOPIC_SUFFIX;
    private static final String ROLE_UPDATED = TOPIC_PREFIX + "RoleUpdated" + TOPIC_SUFFIX;
    private static final String S3_UPLOAD_FAILED = TOPIC_PREFIX + "S3UploadFailed" + TOPIC_SUFFIX;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            log.debug("[KafkaProduceRequiredEventListener] 메시지 생성 이벤트 발행: {}", payload);

            kafkaTemplate.send(MESSAGE_CREATED, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("notificationExecutor")
    @CacheEvict(value = "notificationsByUser", key = "#event.user().id")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoleUpdated(RoleUpdatedEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            log.debug("[KafkaProduceRequiredEventListener] 권한 변경 이벤트 발행: {}", payload);

            kafkaTemplate.send(ROLE_UPDATED, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("notificationExecutor")
    @EventListener
    public void onS3UploadFailed(S3UploadFailedEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            log.debug("[KafkaProduceRequiredEventListener] S3 파일 업로드 실패 이벤트 발행: {}", payload);

            kafkaTemplate.send(S3_UPLOAD_FAILED, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
