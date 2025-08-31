package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private static final String LISTENER_NAME = "[NotificationRequiredEventListener] ";

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final CacheManager notificationCacheManager;

    /**
     * 메시지 생성 이벤트를 처리하여 관련 사용자들에게 알림을 생성합니다.
     * 
     * <p>메시지가 작성된 채널의 모든 참여자(알림 활성화된 사용자)에게
     * 메시지 작성자와 채널 정보를 포함한 알림을 발송합니다.</p>
     * 
     * @param event 메시지 생성 이벤트
     * @throws RuntimeException 알림 생성 중 예외 발생 시
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskListener")
    public void on(MessageCreatedEvent event) {
        Message message = event.message();
        UUID messageId = message.getId();
        UUID channelId = message.getChannel().getId();
        String channelName = message.getChannel().getName();
        UUID authorId = message.getAuthor().getId();

        log.info(LISTENER_NAME + "메시지 알림 생성 시작 - messageId={}, channelId={}, channelName={}", 
                messageId, channelId, channelName);

        try {
            List<User> users = readStatusRepository.findAllByChannelIdWithUserAndNotificationEnabledTrue(channelId)
                    .stream()
                    .map(ReadStatus::getUser)
                    .toList();

            int notificationCount = 0;
            for (User user : users) {
                if (user.getId().equals(authorId)) {
                    continue;
                }

                try {
                    String title = String.format("%s(#%s)", message.getAuthor().getUsername(), channelName);
                    Notification notification = new Notification(user, title, message.getContent());

                    notificationRepository.save(notification);
                    notificationCount++;

                    Objects.requireNonNull(notificationCacheManager.getCache("notificationByUser")).evict(user.getId());

                } catch (Exception userException) {
                    log.error(LISTENER_NAME + "개별 사용자 알림 생성 실패 - userId={}, messageId={}", 
                            user.getId(), messageId, userException);
                }
            }

            log.info(LISTENER_NAME + "메시지 알림 생성 완료 - messageId={}, channelId={}, notificationCount={}", 
                    messageId, channelId, notificationCount);

        } catch (Exception e) {
            log.error(LISTENER_NAME + "메시지 알림 생성 실패 - messageId={}, channelId={}", 
                    messageId, channelId, e);
            throw new RuntimeException("메시지 알림 생성 중 오류 발생", e);
        }
    }

    /**
     * 사용자 권한 변경 이벤트를 처리하여 해당 사용자에게 알림을 생성합니다.
     * 
     * @param event 권한 변경 이벤트
     * @throws RuntimeException 알림 생성 중 예외 발생 시
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskListener")
    public void on(RoleUpdatedEvent event) {
        UUID userId = event.userId();
        Role oldRole = event.oldRole();
        Role newRole = event.newRole();

        log.info(LISTENER_NAME + "권한 변경 알림 생성 시작 - userId={}, oldRole={}, newRole={}", 
                userId, oldRole, newRole);

        try {
            String title = "권한이 변경되었습니다.";
            String content = String.format("%s → %s", oldRole.name(), newRole.name());
            User user = userRepository.findById(userId).orElseThrow();
            Notification notification = new Notification(user, title, content);
            notificationRepository.save(notification);

            Objects.requireNonNull(notificationCacheManager.getCache("notificationByUser")).evict(userId);

            log.info(LISTENER_NAME + "권한 변경 알림 생성 완료 - userId={}, oldRole={}, newRole={}", 
                    userId, oldRole, newRole);

        } catch (Exception e) {
            log.error(LISTENER_NAME + "권한 변경 알림 생성 실패 - userId={}, oldRole={}, newRole={}", 
                    userId, oldRole, newRole, e);
            throw new RuntimeException("권한 변경 알림 생성 중 오류 발생", e);
        }
    }

    /**
     * 바이너리 콘텐츠 업로드 실패 이벤트를 처리하여 관리자들에게 알림을 생성합니다.
     * 
     * @param event 바이너리 콘텐츠 업로드 실패 이벤트
     * @throws RuntimeException 알림 생성 중 예외 발생 시
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("eventTaskListener")
    public void on(S3UploadFailedEvent event) {
        String requestId = event.requestId();
        UUID binaryContentId = event.binaryContentId();

        log.info(LISTENER_NAME + "S3 업로드 실패 알림 처리 시작 - requestId={}, binaryContentId={}", 
                requestId, binaryContentId);

        try {
            List<User> adminUsers = userRepository.findAllByRoleAdmin();

            if (adminUsers.isEmpty()) {
                log.warn(LISTENER_NAME + "ADMIN 권한을 가진 사용자가 없습니다 - requestId={}", requestId);
                return;
            }

            String title = "S3 파일 업로드 실패";
            String content = String.format("RequestId: %s\nBinaryContentId: %s\nError: %s\n", 
                    requestId, binaryContentId, event.reason());

            List<Notification> notifications = adminUsers.stream()
                    .map(adminUser -> new Notification(adminUser, title, content))
                    .toList();

            notificationRepository.saveAll(notifications);

            notifications.forEach(notification -> {
                try {
                    Objects.requireNonNull(notificationCacheManager.getCache("notificationByUser"))
                            .evict(notification.getReceiver().getId());
                } catch (Exception cacheException) {
                    log.warn(LISTENER_NAME + "캐시 무효화 실패 - userId={}, requestId={}", 
                            notification.getReceiver().getId(), requestId, cacheException);
                }
            });

            log.info(LISTENER_NAME + "S3 업로드 실패 알림 처리 완료 - requestId={}, adminCount={}", 
                    requestId, adminUsers.size());

        } catch (Exception e) {
            log.error(LISTENER_NAME + "S3 업로드 실패 알림 생성 중 예상치 못한 오류 발생 - requestId={}", 
                    requestId, e);
            throw new RuntimeException("S3 업로드 실패 알림 생성 중 오류 발생", e);
        }
    }
}
