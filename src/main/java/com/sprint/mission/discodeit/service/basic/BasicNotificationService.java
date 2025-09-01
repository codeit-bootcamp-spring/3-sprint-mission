package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.jpa.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.service.basic
 * FileName     : BasicNotificationService
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */

@RequiredArgsConstructor
@Service
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CacheManager cacheManager;


    @PreAuthorize("principal.userDto.id == #receiverId")
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        List<NotificationDto> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(
                receiverId)
            .stream()
            .map(notification -> notificationMapper.toDto(notification))
            .toList();
        return notifications;
    }

    @PreAuthorize("principal.userDto.id == #receiverId")
    @Transactional
    @Override
    public void delete(UUID notificationId, UUID receiverId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException());
        if (!notification.getReceiverId().equals(receiverId)) {
            throw new IllegalArgumentException();
        }
        notificationRepository.delete(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void create(Set<UUID> receiverIds, String title, String content) {
        if (receiverIds.isEmpty()) {
            return;
        }
        List<Notification> notifications = receiverIds.stream()
            .map(receiverId -> new Notification(
                receiverId,
                title,
                content
            )).toList();
        notificationRepository.saveAll(notifications);
    }
}
