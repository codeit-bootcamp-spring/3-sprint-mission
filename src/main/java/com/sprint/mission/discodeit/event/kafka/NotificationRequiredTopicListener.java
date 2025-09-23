package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {

        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            Channel channel = channelRepository.findById(event.channelId()).orElseThrow(() -> new ChannelNotFoundException(event.channelId()));

            User user = userRepository.findById(event.authorId()).orElseThrow(() -> new UserNotFoundException(event.authorId()));

            String title = (channel.getName() == null)
                    ? user.getUsername()
                    : user.getUsername() + " (#" + channel.getName() + ")";

            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabled(channel.getId(), true);

            log.info("[알림 생성 이벤트 리스너] 찾은 채팅방 알림 true User 수: {}", readStatuses.size());
            List<Notification> notifications = readStatuses.stream()
                    .filter(readStatus -> !readStatus.getUser().getId().equals(user.getId()))
                    .map(readStatus -> new Notification(title, event.content(), readStatus.getUser()))
                    .toList();

            log.info("[알림 생성 이벤트 리스너] 생성한 알림 수: {}", notifications.size());
            notificationRepository.saveAll(notifications);

            // 캐시 무효화
            Cache cache = cacheManager.getCache("userNotifications");
            if (cache != null) {
                notifications.forEach(n -> cache.evict(n.getUser().getId()));
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {

        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            User user = userRepository.findById(event.changedId()).orElseThrow(() -> new UserNotFoundException(event.changedId()));
            String title = "권한이 변경되었습니다.";

            String content = event.oldRole().name() + " -> " + event.newRole().name();

            Notification notification = new Notification(title, content, user);
            notificationRepository.save(notification);

            // 캐시 무효화
            Cache cache = cacheManager.getCache("users");
            if (cache != null) {
                cache.evict(notification.getUser().getId());
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

            List<Notification> notifications = event.users().stream()
                    .map(user -> {
                        return new Notification(
                                "S3 파일 업로드 실패",
                                "RequestsId: " + event.requestId() + "BinaryContentId: " + event.binaryContentId() + "Error: " + event.errorMessage(),
                                user
                        );
                    })
                    .toList();
            notificationRepository.saveAll(notifications);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
