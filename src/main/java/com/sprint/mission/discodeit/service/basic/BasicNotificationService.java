package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.core.exception.SdkServiceException;

@Slf4j
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationDto create(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        log.debug("알림 생성 완료 - id={}, receiverId={}", savedNotification.getId(), savedNotification.getReceiverId());

        return notificationMapper.toDto(savedNotification);
    }

    @Override
    public List<NotificationDto> findAll() {
        List<NotificationDto> notificationDtos =  notificationMapper.toDtoList(notificationRepository.findAll());

        log.debug("조회된 알림 개수: {}", notificationDtos.size());

        return notificationDtos;
    }

    @Transactional
    @Override
    public void delete(UUID notificationId) {
        log.info("알림 확인 요청 처리 시작 - notificationId: {}", notificationId);

        try {
            notificationRepository.deleteById(notificationId);
            log.debug("알림 확인 처리 완료");
        } catch (EmptyResultDataAccessException e) {
            log.warn("알림 확인 요청 처리 중 알림 조회 실패");
            throw new NotificationNotFoundException(notificationId);
        }
    }

    @Transactional
    @Override
    public void notifyAdmin(String title, UUID binaryContentId, Exception ex) {
        String requestId = null;

        if (ex instanceof SdkServiceException sdkEx) {
            requestId = sdkEx.requestId();
        }

        String content = String.format(
            "RequestId: %s%nBinaryContentId: %s%nError: %s",
            requestId,
            binaryContentId,
            ex.getMessage()
        );

        userRepository.findAllIdsByRole(Role.ADMIN).stream()
            .map(adminId -> Notification.of(adminId, title, content))
            .forEach(notification -> {
                notificationRepository.save(notification);
                log.info("관리자 알림 생성 완료: {}", notification);
            });
    }
}