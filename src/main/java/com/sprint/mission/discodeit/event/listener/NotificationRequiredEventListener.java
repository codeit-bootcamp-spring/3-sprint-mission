package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        String senderName = event.userName();
        String channelName = event.channelName();
        String content = event.messageContent();

        log.info("메시지 생성 알림 이벤트 처리 시작 - 발신인: {}, 채널명: {}", senderName, channelName);

        List<ReadStatus> enabledReadStatuses = readStatusRepository.findAllByChannel_IdAndNotificationEnabledTrue(event.channelId());

        enabledReadStatuses.stream()
            .filter(readStatus -> !readStatus.getUser().getUsername().equals(senderName))
            .forEach(readStatus -> {
                    Notification notification = Notification.of(
                        readStatus.getUser().getId(),
                        senderName + " (#" + channelName + ")",
                        content
                    );

                notificationService.create(notification);
            });
    }

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoleUpdated(RoleUpdatedEvent event) {
        log.info("권한 수정 알림 이벤트 처리 시작 - 이전 권한: {}, 변경된 권한: {}", event.fromRole(), event.toRole());

        UUID receiverId = event.userId();
        String fromRole = event.fromRole().name();
        String toRole = event.toRole().name();

        Notification notification = Notification.of(
            receiverId,
            "권한이 변경되었습니다.",
            fromRole + " -> " + toRole
        );

        notificationService.create(notification);
    }
}