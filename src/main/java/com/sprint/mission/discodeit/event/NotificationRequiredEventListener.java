package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;

  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
            event.message().getChannel().getId())
        .stream()
        .filter(rs -> !rs.getUser().getId().equals(event.message().getAuthor().getId()))
        .forEach(rs -> {
          String title = event.message().getAuthor().getUsername()
              + " (#" + event.message().getChannel().getName() + ")";
          String content = event.message().getContent();
          notificationService.create(rs.getUser(), title, content);
        });
  }

  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    String title = "권한이 변경되었습니다.";
    String content = event.oldRole().name() + " -> " + event.newRole().name();
    notificationService.create(event.user(), title, content);
  }
}
