package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationDto> findMyNotifications(UUID receiverId);

    void deleteMyNotification(UUID receiverId, UUID notificationId);

    void createFromMessage(MessageCreatedEvent event);

    void createFromRoleUpdate(RoleUpdatedEvent event);

    void notifyAdmin(S3UploadFailedEvent event);

}
