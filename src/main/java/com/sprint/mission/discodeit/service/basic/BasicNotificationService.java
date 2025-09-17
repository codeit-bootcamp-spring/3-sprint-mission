package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.NotificationsCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.publisher.KafkaEventPublisher;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final String eventName = "notifications.created";

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SseService sseService;
    private final ApplicationEventPublisher eventPublisher;

    @Cacheable(value = "notificationsByUser", key = "#receiverId")
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> findMyNotifications(UUID receiverId) {
        log.info("[notification] findMyNotifications (cacheable) - receiverId={}", receiverId);

        List<NotificationDto> result = notificationRepository.findByReceiver_IdOrderByCreatedAtDesc(
                receiverId)
            .stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());

        log.info("[notification] 응답(DB 쿼리 실행됨): size={}", result.size());
        return result;
    }

    @CacheEvict(value = "notificationsByUser", key = "#receiverId")
    @Override
    public void deleteMyNotification(UUID receiverId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getReceiver().getId().equals(receiverId)) {
            throw new SecurityException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public List<NotificationDto> createFromMessage(UUID channelId, UUID messageId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new MessageNotFoundException(messageId));

        User author = message.getAuthor();

        List<ReadStatus> targets =
            readStatusRepository.findByChannel_IdAndNotificationEnabledTrue(channelId);

        List<Notification> notifications = targets.stream()
            .filter(rs -> !rs.getUser().getId().equals(author.getId()))
            .filter(rs -> rs.getLastReadAt() == null || rs.getLastReadAt()
                .isBefore(message.getCreatedAt()))
            .map(rs -> new Notification(
                rs.getUser(),
                buildMessageTitle(author.getUsername(), channel.getName()),
                buildMessageContent(message.getContent())
            ))
            .toList();

        publishNotifications(notifications,
            () -> log.info("[notification] Message 알림 생성: channel={}, targets={}",
                channel.getName(), notifications.size()));

        return notifications.stream()
            .map(notificationMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public void createFromRoleUpdate(RoleUpdatedEvent event) {
        User user = userRepository.findById(event.userId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + event.userId()));

        Notification notification = new Notification(
            user,
            "권한이 변경되었습니다.",
            event.oldRole() + " -> " + event.newRole()
        );

        publishNotifications(List.of(notification),
            () -> log.info("[notification] RoleUpdatedEvent 알림 생성 - user={}, {} -> {}",
                user.getUsername(), event.oldRole(), event.newRole()));
    }

    @Override
    @Transactional
    public void notifyAdmin(S3UploadFailedEvent event) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        List<Notification> notifications = admins.stream()
            .map(admin -> new Notification(
                admin,
                "[S3 업로드 실패]",
                "RequestId: " + event.requestId()
                    + "\nBinaryContentId: " + event.binaryContentId()
                    + "\nError: " + event.errorMessage()
            ))
            .toList();

        publishNotifications(notifications,
            () -> log.error("[notification] S3UploadFailedEvent 관리자 알림 생성: admins={}, error={}",
                admins.size(), event.errorMessage()));
    }

    private void publishNotifications(List<Notification> notifications, Runnable logAction) {
        if (notifications.isEmpty()) {
            return;
        }

        notificationRepository.saveAll(notifications);
        logAction.run();

        notifications.forEach(n -> {
            NotificationDto dto = notificationMapper.toDto(n);

            // SSE 전송
            sseService.send(
                List.of(n.getReceiver().getId()),
                eventName,
                dto
            );

            // 이벤트 발행
            eventPublisher.publishEvent(
                new NotificationsCreatedEvent(Set.of(n.getReceiver().getId()), dto)
            );
        });
    }

    private String buildMessageTitle(String authorName, String channelName) {
        String label = (channelName == null || channelName.isBlank())
            ? "개인 메시지"
            : "#" + channelName;
        return authorName + " (" + label + ")";
    }

    private String buildMessageContent(String content) {
        if (content == null) {
            return "";
        }
        String oneLine = content.replaceAll("\\s+", " ").trim();
        return oneLine.length() > 80 ? oneLine.substring(0, 80) + "…" : oneLine;
    }

}

