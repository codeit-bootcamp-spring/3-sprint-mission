package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(BinaryContentUploadFailedEvent event) {
    userRepository.findAllByRole(Role.ADMIN)
        .forEach(admin -> {
          String body = "RequestId: " + event.requestId()
              + "\nBinaryContentId: " + event.binaryContentId()
              + "\nError: " + event.errorMessage();
          notificationService.create(admin, "BinaryContent 저장 실패", body);
          log.warn("[Notification created] BinaryContent 저장 실패 알림 전송: {} (receiver: {})", body,
              admin.getEmail());
        });
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
            event.message().getChannel().getId())
        .stream()
        .filter(rs -> !rs.getUser().getId().equals(event.message().getAuthor().getId()))
        .forEach(rs -> {
          String title = event.message().getAuthor().getUsername()
              + " (#" + event.message().getChannel().getName() + ")";
          String content = event.message().getContent();
          Notification notification = notificationService.create(rs.getUser(), title, content);
          log.debug("[Notification created] receiver: {}, title: {}, content: {}",
              notification.getReceiver(), notification.getTitle(), notification.getTitle());
        });
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = event.oldRole().name() + " -> " + event.newRole().name();
    notificationService.create(event.user(), title, content);
  }
}
