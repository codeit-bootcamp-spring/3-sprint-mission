package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Transactional(readOnly = true)
  @Override
  public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 시작: receiverId={}", receiverId);

    List<NotificationDto> notifications = notificationRepository
        .findByReceiverIdOrderByCreatedAtDesc(receiverId)
        .stream()
        .map(notificationMapper::toDto)
        .toList();

    log.info("알림 목록 조회 완료: receiverId={}, count={}", receiverId, notifications.size());
    return notifications;
  }

  @Transactional
  @Override
  public void deleteByIdAndReceiverId(UUID notificationId, UUID receiverId) {
    log.debug("알림 삭제 시작: notificationId={}, receiverId={}", notificationId, receiverId);

    var notification = notificationRepository.findByIdAndReceiverId(notificationId, receiverId)
        .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: notificationId={}", notificationId);
  }

  // BasicNotificationService에 추가
  public boolean isNotificationOwner(UUID notificationId, UUID userId) {
    return notificationRepository.findByIdAndReceiverId(notificationId, userId)
        .isPresent();
  }
}
