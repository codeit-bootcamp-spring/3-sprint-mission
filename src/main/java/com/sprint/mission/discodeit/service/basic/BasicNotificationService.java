package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

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

    @CacheEvict(value = "notificationsByUser", key = "#notificationId")
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
    public void createFromMessage(MessageCreatedEvent event) {
        // 1. 채널 알림이 활성화된 사용자 조회
        List<ReadStatus> targets = readStatusRepository
            .findByChannel_IdAndNotificationEnabledTrue(event.channelId());

        // 2. 본인(authorId)은 알림 제외
        List<Notification> notifications = targets.stream()
            .map(ReadStatus::getUser)
            .filter(user -> !user.getId().equals(event.authorId()))
            .map(user -> new Notification(
                user,
                buildMessageTitle(event.authorName(), event.channelName()),
                buildMessageContent(event.content())
            ))
            .toList();

        // 3. 저장
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.info("[notification] MessageCreatedEvent 알림 생성: channel={}, targets={}",
                event.channelName(), notifications.size());
        }
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

        notificationRepository.save(notification);
        log.info("[notification] RoleUpdatedEvent 알림 생성 - user={}, {} -> {}",
            user.getUsername(), event.oldRole(), event.newRole());
    }

    @Override
    @Transactional
    public void notifyAdmin(S3UploadFailedEvent event) {
        // 관리자(User.ADMIN) 계정 조회
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

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.error("[notification] S3UploadFailedEvent 관리자 알림 생성: admins={}, error={}",
                admins.size(), event.errorMessage());
        }
    }

    private String buildMessageTitle(String authorName, String channelName) {
        return authorName + " (#" + (channelName != null ? channelName : "개인 메시지") + ")";
    }

    private String buildMessageContent(String content) {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}

