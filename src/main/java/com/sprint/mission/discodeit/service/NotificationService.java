package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final SseService sseService;

  @Transactional
  @CacheEvict(value = "notifications", key = "#receiver.id")
  public Notification create(User receiver, String title, String content) {
    Notification notification = Notification.create(receiver, title, content);
    Notification saved = notificationRepository.save(notification);

    sseService.send(
        List.of(receiver.getId()),
        "notifications.created",
        NotificationDto.from(saved)
    );
    return saved;
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "notifications", key = "#receiverId")
  public List<Notification> findAllByReceiverId(UUID receiverId) {
    return notificationRepository.findAllByReceiverId(receiverId);
  }

  @Transactional
  @CacheEvict(value = "notifications", key = "#receiverId")
  public void delete(UUID notificationId, UUID receiverId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotificationNotFoundException(notificationId.toString()));
    if (!notification.getReceiver().getId().equals(receiverId)) {
      throw new DiscodeitException(
          ErrorCode.NOTIFICATION_PERMISSION_DENIED,
          "알림 삭제 권한이 없습니다.");
    }
    notificationRepository.delete(notification);
  }
}
