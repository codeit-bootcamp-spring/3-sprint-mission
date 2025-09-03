package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = "notifications", key = "#user.id")
    @Override
    public void create(User user, String title, String content) {
        Notification notification = new Notification(user, title, content);
        notificationRepository.save(notification);
        log.info("알림 생성완료! id:{}", notification.getId());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "notifications", key = "#userId")
    @Override
    public List<NotificationDto> findAll(UUID userId) {
        return notificationRepository.findAllByUserId(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();

    }

    @Transactional
    @PreAuthorize("@basicNotificationService.isOwner(#notificationId,#userId)")
    @CacheEvict(value = "notifications", key = "#userId")
    @Override
    public void deleteNotification(UUID notificationId, UUID userId) {
        notificationRepository.deleteById(notificationId);
    }

    public boolean isOwner(UUID notificationId, UUID userId){
        if(notificationId == null){
            return false;
        }
        UUID notificationUserId = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new).getUser().getId();
        return userId.equals(notificationUserId);
    }


}
