package com.sprint.mission.discodeit.service.basic;

import static com.sprint.mission.discodeit.config.CacheConfig.NOTIFICATIONS_BY_USER;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @CacheEvict(cacheNames = NOTIFICATIONS_BY_USER, key = "#receiverId")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createNotification(UUID receiverId, String title, String content) {

        log.info("알림 생성 중 - receiverId: {}, title: {}, content: {}", receiverId, title, content);

        Notification notification = new Notification(receiverId, title, content);

        Notification savedNotification = notificationRepository.save(notification);

        log.info("알림 생성 완료 - notificationId: {}", savedNotification.getId());
    }

    @Cacheable(value = NOTIFICATIONS_BY_USER, key = "#userId",
        unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> findAllById(UUID userId) {

        log.info("알림 목록 조회 - userId: {}", userId);

        return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId)
            .stream().map(notificationMapper::toDto).toList();
    }

    @CacheEvict(cacheNames = NOTIFICATIONS_BY_USER, key = "#userId")
    @Transactional
    @Override
    public void deleteMyNotification(UUID notificationId, UUID userId) {

        log.info("알림 확인 - notificationId: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException("존재하지 않는 알림입니다"));

        if (!notification.getReceiverId().equals(userId)) {
            throw new NotificationAccessDeniedException("다른 사용자의 알림입니다");
        }

        notificationRepository.delete(notification);
    }
}
