package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentNotUploadedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private static final String LISTENER_NAME = "[NotificationRequiredEventListener] ";

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationTaskExecutor")
    public void on(MessageCreatedEvent event) {
        Message message = event.message();

        Channel channel = message.getChannel();
        String channelName = channel.getName();
        UUID channelId = channel.getId();

        User author = message.getAuthor();
        UUID authorId = author.getId();

        try {
            List<User> users = readStatusRepository.findAllByChannelIdWithUserAndNotificationEnabledTrue(channelId)
                    .stream()
                    .map(ReadStatus::getUser)
                    .toList();

            for (User user : users) {
                // 메시지 작성자는 알림 제외
                if (user.getId().equals(authorId)) {
                    continue;
                }

                String title = author.getUsername() + "(#" + channelName + ")";
                Notification notification = new Notification(
                        user,
                        title,
                        message.getContent());

                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            log.error(LISTENER_NAME + "메시지 알림 생성 실패: messageId={}, channelId={}", message.getId(), channelId, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationTaskExecutor")
    public void on(RoleUpdatedEvent event) {
        User user = event.user();
        Role oldRole = event.oldRole();
        Role newRole = event.newRole();

        try {
            String title = "권한이 변경되었습니다.";
            String content = oldRole.name() + " -> " + newRole.name();

            Notification notification = new Notification(
                    user,
                    title,
                    content
            );

            notificationRepository.save(notification);
        } catch (Exception e) {
            log.error(LISTENER_NAME + "권한 변경 알림 생성 실패: userId={}", user.getId(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("notificationTaskExecutor")
    public void on(BinaryContentNotUploadedEvent event) {

        try {
            log.info(LISTENER_NAME + "S3 업로드 실패 알림 처리 시작 - requestId={}", event.requestId());

            // ADMIN 권한을 가진 모든 사용자 조회
            List<User> adminUsers = userRepository.findAllByRoleAdmin();

            if (adminUsers.isEmpty()) {
                log.warn(LISTENER_NAME + "ADMIN 권한을 가진 사용자가 없습니다 - requestId={}", event.requestId());
                return;
            }

            String title = "S3 파일 업로드 실패";
            String content = String.format("RequestId: %s\nBinaryContentId: %s\nError: %s\n",
                    event.requestId(),
                    event.binaryContentId(),
                    event.reason()
                    );

            // 모든 ADMIN 사용자에게 알림 발송
            List<Notification> notifications = adminUsers.stream()
                    .map(adminUser -> new Notification(adminUser, title, content))
                    .toList();

            notificationRepository.saveAll(notifications);

            log.info(LISTENER_NAME + "S3 업로드 실패 알림 처리 완료 - requestId={}, adminCount={}",
                    event.requestId(), adminUsers.size());
        } catch (Exception e) {
            log.error(LISTENER_NAME + "S3 업로드 실패 알림 생성 중 예상치 못한 오류 발생 - requestId={}", event.requestId(), e);
        }
    }
}
