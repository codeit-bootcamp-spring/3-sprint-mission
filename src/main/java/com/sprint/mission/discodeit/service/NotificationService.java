package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    NotificationDto create(@NotNull Notification notification);

    List<NotificationDto> findAll();

    List<NotificationDto> findAllByUserId(@NotNull UUID userId);

    void delete(@NotNull UUID notificationId);

    void notifyAdmin(@NotBlank String title, @NotNull UUID binaryContentId, @NotNull String content);
}
