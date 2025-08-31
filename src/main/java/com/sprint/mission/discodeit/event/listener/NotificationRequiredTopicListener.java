package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            List<ReadStatus> enabledReadStatuses =
                readStatusRepository.findAllByChannel_IdAndNotificationEnabledTrue(event.channelId());

            enabledReadStatuses.stream()
                .filter(rs -> !rs.getUser().getUsername().equals(event.userName()))
                .forEach(rs -> notificationService.create(
                    Notification.of(
                        rs.getUser().getId(),
                        event.userName() + " (#" + event.channelName() + ")",
                        event.messageContent()
                    )
                ));
        } catch (JsonProcessingException  e) {
            log.error("메시지 생성 이벤트 처리 실패 - payload={}", kafkaEvent, e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            Notification notification = Notification.of(
                event.userId(),
                "권한이 변경되었습니다.",
                event.fromRole().name() + " -> " + event.toRole().name()
            );
            notificationService.create(notification);
        } catch (JsonProcessingException e) {
            log.error("권한 수정 이벤트 처리 실패 - payload={}", kafkaEvent, e);
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

            String content = String.format(
                "RequestId: %s%nBinaryContentId: %s%nError: %s",
                event.requestId(),
                event.binaryContentId(),
                event.errorMessage()
            );

            notificationService.notifyAdmin(
                "S3 파일 업로드 실패",
                event.binaryContentId(),
                content
            );
        } catch (JsonProcessingException e) {
            log.error("S3 업로드 실패 이벤트 처리 실패 - payload={}", kafkaEvent, e);
            throw new RuntimeException(e);
        }
    }
}