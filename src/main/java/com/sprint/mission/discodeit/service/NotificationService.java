package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {

    NotificationDto create(Notification notification);

    List<NotificationDto> findByUserId(UUID receiverId);

    void delete(UUID notificationId, UUID receiverId);
}
