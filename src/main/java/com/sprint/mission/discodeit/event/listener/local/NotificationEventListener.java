package com.sprint.mission.discodeit.event.listener.local;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "feature.notification.local-listener-enabled", havingValue = "true")
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final UserService userService;
    private final ReadStatusRepository readStatusRepository;

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {

        log.debug("[NotificationRequiredEventListener] 메세지 생성 알림 이벤트 리스너 시작");

        List<UUID> receiverIds = readStatusRepository
            .findUserIdsByChannelIdAndNotificationEnabledTrue(event.channelId());

        log.debug("알림을 받는 유저: {}", receiverIds);

        receiverIds.stream()
            .filter(receiverId -> !receiverId.equals(event.authorId()))
            .forEach(receiverId -> {
                String title = String.format("%s (%s)",
                    event.authorName(), buildTitleName(event.channelName())
                );
                String content = event.content();

                notificationService.createNotification(receiverId, title, content);
            });
    }

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {

        log.debug("[NotificationRequiredEventListener] 권한 변경 알림 이벤트 리스너 시작");

        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.oldRole(), event.newRole());

        notificationService.createNotification(event.userId(), title, content);
    }

    @Async("asyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(BinaryStorageFailedEvent event) {

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
    }

    private static String buildTitleName(String channelName) {
        return (channelName != null) ? channelName : "Private";
    }
}
