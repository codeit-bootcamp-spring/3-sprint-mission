package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.service.basic
 * FileName     : NotificationService
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
public interface NotificationService {
    List<NotificationDto> findAllByReceiverId(UUID receiverId);

    void delete(UUID notificationId, UUID receiverId);

    void create(Set<UUID> receiverIds, String title, String content);
}
