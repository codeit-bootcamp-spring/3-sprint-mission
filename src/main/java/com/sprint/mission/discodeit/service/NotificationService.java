package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    // 유저의 알림 조회
    List<NotificationDto> findAllNotifications(UUID receiverId);

    // 알림 읽음 처리
    void deleteNotification(UUID notificationId, UUID requestId);

    void saveAllNotifications(List<Notification> notifications);

    // 관리자에게 알림 가도록
    void notifyS3StoreFail(String jobName, UUID binaryContentId, String requestId, String errorMessage);
}
