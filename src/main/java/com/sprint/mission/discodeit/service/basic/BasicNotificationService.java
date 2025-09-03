package com.sprint.mission.discodeit.service.basic;

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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CacheManager cacheManager;

    @Transactional
    @Override
    public NotificationDto create(UUID receiverId, String title, String content) {
        log.info("[CACHE-TEST / 알림 생성 시도] 수신자: {}, 제목: {}", receiverId, title);

        Notification notification = new Notification(receiverId, title, content);
        notificationRepository.save(notification);

        // CacheManager를 사용해 해당 사용자의 알림 캐시 수동 무효화
        var cache = cacheManager.getCache("userNotifications");
        if (cache != null) {
            boolean hadValue = cache.get(receiverId) != null;
            cache.evict(receiverId);
            log.info("[CACHE-EVICT / 수동 캐시 무효화] 사용자: {}, 기존 캐시 존재 여부 : {}", receiverId, hadValue);
        } else {
            log.warn("[CACHE-WARNING] userNotifications 캐시를 찾을 수 없음");
        }

        log.info("[CACHE-TEST / 알림 생성 성공] ID: {}, 수신자: {}", notification.getId(), receiverId);
        return notificationMapper.toDto(notification);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "userNotifications", key = "#receiverId" )
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.info("[CACHE-MISS / 알림 목록 조회] 수신자: {} ( 캐시 미스 ! )", receiverId);

        List<NotificationDto> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
            .stream()
            .map(notificationMapper::toDto)
            .toList();

        log.info("[CACHE-STORE / 알림 목록 조회 완료] 수신자: {}, 알림 수: {}개", receiverId, notifications.size());
        return notifications;
    }

    @Transactional
    @Override
    @CacheEvict(value = "userNotifications", key = "#receiverId" )
    public void delete(UUID notificationId, UUID receiverId) {
        log.info("[CACHE-TEST / 알림 삭제 시도] ID: {}, 요청자: {}", notificationId, receiverId);

        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> {
                log.error("[알림 삭제 실패] 알림을 찾을 수 없습니다. ID: {}", notificationId);
                return new NotificationNotFoundException();
            });

        // 권한 검증: 본인의 알림만 삭제 가능
        if (!notification.getReceiverId().equals(receiverId)) {
            log.error("[알림 삭제 실패] 권한이 없습니다. 알림 ID: {}, 소유자: {}, 요청자: {}",
                notificationId, notification.getReceiverId(), receiverId);
            throw new NotificationAccessDeniedException();
        }

        notificationRepository.delete(notification);
        log.info("[CACHE-EVICT] @CacheEvict 어노테이션에 의한 자동 캐시 무효화 - 사용자: {}", receiverId);
        log.info("[CACHE-TEST / 알림 삭제 성공] ID: {}, 삭제자: {}", notificationId, receiverId);
    }
}
