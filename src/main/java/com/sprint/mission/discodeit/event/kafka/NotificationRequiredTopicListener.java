package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            Set<UUID> receiverIds = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
                            event.channelId())
                    .stream().map(rs -> rs.getUser().getId())
                    .filter(id -> !id.equals(event.authorId()))
                    .collect(Collectors.toSet());
            String title = (event.channelType() == ChannelType.PUBLIC)
                    ? event.authorUsername() + " (#" + event.channelName() + ")"
                    : event.authorUsername();

            notificationService.create(receiverIds, title, event.content());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            String title = "권한이 변경되었습니다.";
            String content = event.role().name() + " -> " + event.newRole().name();
            notificationService.create(Set.of(event.userid()), title, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
            String requestId = event.getRequestId();
            UUID binaryContentId = event.getBinaryContentId();
            Throwable e = event.getE();

            String title = "S3 파일 업로드 실패";

            String content = """
                    RequestId: %s
                    BinaryContentId: %s
                    Error: %s
                    """.formatted(requestId, binaryContentId, e.getMessage());

            Set<UUID> adminIds = new HashSet<>(userRepository.findAllIdsByRole(Role.ADMIN));

            notificationService.create(adminIds, title, content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
