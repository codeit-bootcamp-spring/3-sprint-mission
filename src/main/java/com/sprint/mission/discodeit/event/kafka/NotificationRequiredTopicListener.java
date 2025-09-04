package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.NotFoundChannelException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.struct.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final SseService sseService;
    private final NotificationMapper notificationMapper;

    private static final String EVENT_NAME_NOTIFICATION_CREATED = "notifications.created";
    private static final String ROLE_UPDATE_TITLE = "권한이 변경되었습니다.";
    private static final String PRIVATE_CHANNEL_NAME = "개인 메시지";
    private static final String S3_UPLOAD_FAIL_TITLE = "S3 업로드 실패";
    private final UserMapper userMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreated(String kafkaEvent) {

        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                MessageCreatedEvent.class);
            MessageResponseDto message = event.data();

            UserResponseDto author = message.author();
            UUID channelId = message.channelId();
            String title = getTitle(author, channelId);
            String content = message.content();

            // 알림 수신 여부가 true인 채널의 readStatus 조회
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(
                channelId, true
            );

            // 캐시 무효화
            Cache cache = cacheManager.getCache("notificationsByUser");
            if (cache != null) {
                readStatuses.stream()
                    .map(ReadStatus::getUser)
                    .map(User::getId)
                    .filter(userId -> !userId.equals(author.id()))
                    .distinct()
                    .forEach(cache::evict);
            }

            log.debug("[NotificationRequiredEventListener] 알림 수신 가능 사용자 수: {}",
                readStatuses.size());

            // 메시지를 보낸 사용자는 알림 대상에서 제외
            List<Notification> notifications = readStatuses.stream()
                .filter(readStatus -> !userMapper.toDto(readStatus.getUser()).equals(author))
                .map(readStatus -> new Notification(title, content, readStatus.getUser()))
                .toList();

            List<Notification> saved = notificationRepository.saveAll(notifications);

            log.debug("[NotificationRequiredEventListener] 알림 {}개 생성 완료", notifications.size());

            // 저장 후 SSE 전송 (각 수신자별 개별 이벤트)
            sendSseEvent(saved);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdated(String kafkaEvent) {

        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            User user = event.user();
            Role oldRole = event.oldRole();
            Role newRole = event.newRole();

            String content = oldRole.name() + " -> " + newRole.name();

            Notification notification = Notification.builder()
                .title(ROLE_UPDATE_TITLE)
                .content(content)
                .user(user)
                .build();

            Notification savedNotification = notificationRepository.save(notification);
            // 저장 후 SSE 전송 (각 수신자별 개별 이벤트)
            sendSseEvent(List.of(savedNotification));

            log.debug("[NotificationRequiredEventListener] 권한 변경 알림 생성 완료- id: {}",
                savedNotification.getId());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailed(String kafkaEvent) {

        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                S3UploadFailedEvent.class);

            String content =
                "Request Id: " + event.requestId() + "\n BinaryContentId: "
                    + event.binaryContentId()
                    + "\n Error: "
                    + event.errorMessage();

            List<User> admins = userRepository.findByRole(Role.ADMIN);

            List<Notification> notifications = admins.stream()
                .map(user -> new Notification(S3_UPLOAD_FAIL_TITLE, content, user))
                .toList();

            // 캐시 무효화
            Cache cache = cacheManager.getCache("notificationsByUser");
            if (cache != null) {
                admins.stream()
                    .map(User::getId)
                    .distinct()
                    .forEach(cache::evict);
            }

            List<Notification> saved = notificationRepository.saveAll(notifications);

            log.debug("[NotificationRequiredEventListener] S3 업로드 실패 알림 전송 완료- {}개",
                notifications.size());

            // 저장 후 SSE 전송 (각 수신자별 개별 이벤트)
            sendSseEvent(saved);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTitle(UserResponseDto author, UUID channelId) {
        StringBuilder title = new StringBuilder(author.username()).append(" (#");

        Channel channel = findChannel(channelId);

        if (channel.getType().equals(ChannelType.PUBLIC)) {
            title.append(channel.getName());
        } else {
            title.append(PRIVATE_CHANNEL_NAME);
        }
        title.append(")");

        return title.toString();
    }

    private Channel findChannel(UUID channelId) {
        return channelRepository.findById(channelId)
            .orElseThrow(() -> new NotFoundChannelException(channelId));
    }

    private void sendSseEvent(List<Notification> notifications) {
        for (Notification n : notifications) {
            NotificationDto dto = notificationMapper.toDto(n);
            sseService.send(
                List.of(n.getUser().getId()),
                EVENT_NAME_NOTIFICATION_CREATED,
                dto
            );
        }
    }
}
