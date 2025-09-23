package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createNotification(UUID receiverId, String title, String content);

    List<NotificationDto> findAllById(UUID userId);

    void deleteMyNotification(UUID notificationId, UUID userId);
}
