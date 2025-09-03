package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreateEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                MessageCreatedEvent.class);
            notificationService.createFromMessage(event);
            log.info("[Kafka] MessageCreatedEvent 처리 완료: {}", event);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] MessageCreatedEvent 역직렬화 실패", e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            notificationService.createFromRoleUpdate(event);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] RoleUpdatedEvent 역직렬화 실패", e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                S3UploadFailedEvent.class);
            notificationService.notifyAdmin(event);
        } catch (JsonProcessingException e) {
            log.error("[Kafka] S3UploadFailedEvent 역직렬화 실패", e);
        }
    }

}
