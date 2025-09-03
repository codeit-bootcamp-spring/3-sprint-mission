package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

//@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * 메시지 생성 알림: 채널 구독자(알림 활성)에게, 작성자 본인은 제외
     */
    @CacheEvict(value = "notificationsByUser", key = "#notification.receiver.id")
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(MessageCreatedEvent event) {
        List<ReadStatus> targets = readStatusRepository
            .findByChannel_IdAndNotificationEnabledTrue(event.channelId());

        List<Notification> notifications = targets.stream()
            .map(ReadStatus::getUser)
            .filter(u -> !u.getId().equals(event.authorId()))
            .map(u -> new Notification(
                u,
                buildMessageTitle(event.authorName(), event.channelName()),
                buildMessageContent(event.content())
            ))
            .collect(Collectors.toList());

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
            log.info("[NotificationsEvent] 메시지 알림 생성: channel={}, targets={}", event.channelName(),
                notifications.size());
        }
    }

    /**
     * 권한 변경 알림: 당사자에게
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        User target = userRepository.findById(event.userId()).orElse(null);
        if (target == null) {
            return;
        }

        Notification n = new Notification(
            target,
            "권한이 변경되었습니다.",
            event.oldRole() + "->" + event.newRole()
        );
        notificationRepository.save(n);
        log.info("[NotificationEvent] 권한 변경 알림 생성: userId={}, {} -> {}",
            event.userId(), event.oldRole(), event.newRole());
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
