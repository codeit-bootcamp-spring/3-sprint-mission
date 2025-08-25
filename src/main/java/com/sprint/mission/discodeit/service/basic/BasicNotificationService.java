package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public NotificationDto create(User user, String title, String content) {
        Notification notification = new Notification(user, title, content);
        notificationRepository.save(notification);
        log.info("알림 생성완료! id:{}", notification.getId());
        return notificationMapper.toDto(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> findAll(String bearerToken) {
        String accessToken = resolveToken(bearerToken);
        UUID userId = jwtTokenProvider.getUserId(accessToken);

        return notificationRepository.findAllByUserId(userId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();

    }

    @Transactional
    @PreAuthorize("@basicNotificationService.isOwner(#notificationId,#bearerToken)")
    @Override
    public void deleteNotification(UUID notificationId, String bearerToken) {
        notificationRepository.deleteById(notificationId);
    }

    public boolean isOwner(UUID notificationId, String bearerToken){
        if(notificationId == null){
            return false;
        }
        String accessToken = resolveToken(bearerToken);
        UUID userId = jwtTokenProvider.getUserId(accessToken);
        UUID notificationUserId = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new).getUser().getId();
        return userId.equals(notificationUserId);
    }



    private String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
