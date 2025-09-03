package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(UUID receiverId, String title, String content) {
        Notification notification = Notification.builder()
            .receiverId(receiverId)
            .title(title)
            .content(content)
            .build();
        notificationRepository.save(notification);
        notificationRepository.flush();
        log.info("[Notification] saved: rid={}", receiverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        return notificationRepository.findAllByReceiverId(receiverId).stream()
            .map(n -> new NotificationDto(
                n.getId(),
                n.getCreatedAt(),
                n.getReceiverId(),
                n.getTitle(),
                n.getContent()
            ))
            .toList();
    }

    @Override
    public void confirmByOwner(UUID notificationId, UUID requesterId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

        if (!notification.getReceiverId().equals(requesterId)) {
            throw new DiscodeitException(ErrorCode.FORBIDDEN);
        }

        notificationRepository.delete(notification);
    }
}
