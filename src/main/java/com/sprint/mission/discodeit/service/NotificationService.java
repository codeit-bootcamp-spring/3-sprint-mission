package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {

    List<NotificationDto> findByUserId(UUID receiverId);

    void delete(UUID notificationId, UUID receiverId);
}
