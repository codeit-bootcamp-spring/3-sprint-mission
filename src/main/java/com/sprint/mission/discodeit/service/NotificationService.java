package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationDto> findAllByReceiverId(UUID userId);

    void confirm(UUID notificationId, UUID userId);

    void send(User receiver, String title, String content);

    void sendToAdmin(String title, String content);
}