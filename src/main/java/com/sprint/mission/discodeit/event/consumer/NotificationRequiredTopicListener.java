package com.sprint.mission.discodeit.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * kafka 브로커에서 메시지를 구독
 * 메시지를 역직렬화 -> NotificationService 호출
 * 카프카를 통해 전달된 이벤트를 다른 인스턴스에서도 동일하게 처리
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreated(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            notificationService.createFromMessage(event.channelId(), event.messageId());
            log.info("[Kafka] MessageCreatedEvent 처리 완료: {}", event);
        } catch (Exception e) {
            log.error("[Kafka] MessageCreatedEvent 처리 실패", e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdated(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            notificationService.createFromRoleUpdate(event);
            log.info("[Kafka] RoleUpdatedEvent 처리 완료: {}", event);
        } catch (Exception e) {
            log.error("[Kafka] RoleUpdatedEvent 처리 실패", e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailed(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
            notificationService.notifyAdmin(event);
            log.info("[Kafka] S3UploadFailedEvent 처리 완료: {}", event);
        } catch (Exception e) {
            log.error("[Kafka] S3UploadFailedEvent 처리 실패", e);
        }
    }
}
