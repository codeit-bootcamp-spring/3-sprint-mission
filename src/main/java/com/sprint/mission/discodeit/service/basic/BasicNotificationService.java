package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Cacheable(value = "userNotifications", keyGenerator = "userIdKeyGenerator")
    @Override
    public List<NotificationDto> getNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new UserNotFoundException(name));

        return notificationRepository.findAllByUserId(user.getId()).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @CacheEvict(value = "userNotifications", keyGenerator = "userIdKeyGenerator")
    @Override
    public void deleteNotification(UUID notificationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UserNotFoundException(authentication.getName()));

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getUser().equals(user)) throw new AuthorizationDeniedException("Access denied");

        notificationRepository.delete(notification);
    }
}
