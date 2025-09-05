package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final RedisCacheManager cacheManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public NotificationDto create(User user, String title, String content) {
        Notification notification = new Notification(user, title, content);
        notificationRepository.save(notification);

        Cache cache = cacheManager.getCache("notificationByUser");
        if (cache != null) cache.evict(user.getId());

        return notificationMapper.toDto(notification);
    }

    @Override
    @Cacheable(value = "notificationByUser", key = "#receiverId")
    public List<NotificationDto> findByUserId(UUID receiverId) {

        List<Notification> notificationList = notificationRepository.findByReceiverId(receiverId);

        return notificationList.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Override
    @CacheEvict(value = "notificationByUser", key = "#receiverId")
    public void delete(UUID notificationId, UUID receiverId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new DiscodeitException("알림이 없습니다.", Instant.now(), ErrorCode.NOTIFICATION_NOT_FOUND, null));

        if (!notification.getReceiver().getId().equals(receiverId)) {
            throw new DiscodeitException("권한이 없습니다.", Instant.now(), ErrorCode.FORBIDDEN_ACCESS, null);
        }

        notificationRepository.delete(notification);
    }
}
