package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDto create(User user, String title, String content);

    List<NotificationDto> findAll(String bearerToken);

    void deleteNotification(UUID notificationId, String bearerToken);


}
