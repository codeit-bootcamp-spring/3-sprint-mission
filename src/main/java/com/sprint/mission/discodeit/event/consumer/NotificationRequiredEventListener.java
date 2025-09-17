package com.sprint.mission.discodeit.event.consumer;

import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 로컬 이벤트 기반
 * 이벤트 소스: 같은 애플리케이션 내부에서만 전파
 * 단일 인스턴스 환경
 */
//@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredEventListener {

    private final NotificationService notificationService;
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
        log.debug("[NotificationEventListener] MessageCreatedEvent 수신: {}", event);
        notificationService.createFromMessage(event.channelId(), event.messageId());
    }

    /**
     * 권한 변경 알림: 당사자에게
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(RoleUpdatedEvent event) {
        log.debug("[NotificationEventListener] RoleUpdatedEvent 수신: {}", event);
        notificationService.createFromRoleUpdate(event);
    }

    /**
     * S3 업로드 실패 알림
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(S3UploadFailedEvent event) {
        log.debug("[LocalEvent] S3UploadFailedEvent 수신: {}", event);
        notificationService.notifyAdmin(event);
    }

    /**
     * 캐시 무효화 - 메시지 알림 생성 시
     */
    @CacheEvict(value = "notificationsByUser", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictOnMessage(MessageCreatedEvent event) {
        log.debug("[LocalEvent] notificationsByUser 캐시 무효화 (MessageCreatedEvent)");
    }

    /**
     * 캐시 무효화 - 권한 변경 시
     */
    @CacheEvict(value = "notificationsByUser", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictOnRole(RoleUpdatedEvent event) {
        log.debug("[LocalEvent] notificationsByUser 캐시 무효화 (RoleUpdatedEvent)");
    }

    /**
     * 캐시 무효화 - S3 업로드 실패 시
     */
    @CacheEvict(value = "notificationsByUser", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictOnS3(S3UploadFailedEvent event) {
        log.debug("[LocalEvent] notificationsByUser 캐시 무효화 (S3UploadFailedEvent)");
    }

}
