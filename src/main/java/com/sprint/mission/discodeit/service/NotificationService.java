package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
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

    @PreAuthorize("principal.userDto.id == #receiverId")
    @Transactional(readOnly = true)
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        return notificationRepository.findAllByReceiverId(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @PreAuthorize("principal.userDto.id == #receiverId")
    @Transactional
    public void delete(UUID notificationId, UUID receiverId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));
        if (!notification.getReceiverId().equals(receiverId)) {
            throw new DiscodeitException(ErrorCode.NOTIFICATION_PERMISSION_DENIED);
        }
        notificationRepository.delete(notification);
    }

}
