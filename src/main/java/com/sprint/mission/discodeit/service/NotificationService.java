package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  @Transactional
  public Notification create(User receiver, String title, String content) {
    Notification notification = Notification.create(receiver, title, content);
    return notificationRepository.save(notification);
  }

  @Transactional(readOnly = true)
  public List<Notification> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId);
  }

  @Transactional
  public void delete(UUID notificationId, UUID receiverId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId.toString()));
    if (!notification.getReceiver().getId().equals(receiverId)) {
      throw new com.sprint.mission.discodeit.exception.DiscodeitException(
          com.sprint.mission.discodeit.exception.ErrorCode.NOTIFICATION_PERMISSION_DENIED,
          "알림 삭제 권한이 없습니다.");
    }
    notificationRepository.delete(notification);
  }
}
