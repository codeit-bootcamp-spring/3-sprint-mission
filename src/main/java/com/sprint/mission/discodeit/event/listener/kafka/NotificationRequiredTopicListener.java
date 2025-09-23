package com.sprint.mission.discodeit.event.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryStorageFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final UserService userService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                MessageCreatedEvent.class);

            List<UUID> receiverIds = readStatusRepository
                .findUserIdsByChannelIdAndNotificationEnabledTrue(event.channelId());

            receiverIds.stream()
                .filter(receiverId -> !receiverId.equals(event.authorId()))
                .forEach(receiverId -> {
                    String title = String.format("%s (%s)",
                        event.authorName(), buildTitleName(event.channelName())
                    );
                    String content = event.content();

                    notificationService.createNotification(receiverId, title, content);
                });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            String title = "권한이 변경되었습니다.";
            String content = String.format("%s -> %s", event.oldRole(), event.newRole());

            notificationService.createNotification(event.userId(), title, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.BinaryStorageFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            BinaryStorageFailedEvent event = objectMapper.readValue(kafkaEvent,
                BinaryStorageFailedEvent.class);

            String title = "S3 파일 업로드 실패";
            String content = """
                Task: S3BinaryContentStorage#put
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """.formatted(event.requestId(), event.binaryContentId(), event.errorSummary());

            List<UUID> adminIds = userService.findAdminIds();
            for (UUID adminId : adminIds) {
                notificationService.createNotification(adminId, title, content);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildTitleName(String channelName) {
        return (channelName != null) ? channelName : "Private";
    }

}
