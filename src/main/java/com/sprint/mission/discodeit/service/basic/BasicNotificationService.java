package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotFoundNotificationException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.struct.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Cacheable(value = "notificationsByUser", keyGenerator = "userIdKeyGenerator")
    public List<NotificationDto> getNotifications() {
        User user = getUser();

        log.info("[BasicNotificationService] 사용자: {}의 알림 조회", user.getUsername());

        List<NotificationDto> result = notificationRepository.findAllByUserId(user.getId()).stream()
            .map(notificationMapper::toDto)
            .toList();

        log.info("[BasicNotificationService] 알림 조회 완료- 총 {}개 ", result.size());

        return result;
    }

    @Override
    @CacheEvict(value = "notificationsByUser", keyGenerator = "userIdKeyGenerator")
    public void checkNotifications(UUID notificationId) {
        User user = getUser();

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotFoundNotificationException(notificationId));

        UUID userId = user.getId();
        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 알림만 확인 가능합니다.");
        }

        notificationRepository.deleteById(notificationId);

        log.info("[BasicNotificationService] 알림 읽음 처리 완료- notificationId: {}", notificationId);
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        return userRepository.findByUsername(name)
            .orElseThrow(() -> new NotFoundUserException(name));
    }
}
