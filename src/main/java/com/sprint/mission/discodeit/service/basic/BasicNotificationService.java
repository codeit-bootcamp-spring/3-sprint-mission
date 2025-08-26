package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<NotificationDto> findAllByReceiverId(UUID userId) {
        return notificationRepository.findAllByReceiverIdAndConfirmedFalse(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }


    @Override
    @Transactional
    public void confirm(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new NotificationAccessDeniedException(notificationId);
        }

        notification.confirm();
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(User receiver, String title, String content) {
        Notification notification = new Notification(receiver, title, content);
        notificationRepository.save(notification);
        log.info("알림 저장 완료 → to={}, title={}", receiver.getUsername(), title);
    }

    @Override
    public void sendToAdmin(String title, String content) {
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("관리자 유저를 찾을 수 없습니다"));
        send(admin, title, content);
    }
}