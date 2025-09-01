package com.sprint.mission.discodeit.dto.notification;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.notification
 * FileName     : NotificationDto
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
public record NotificationDto(
    UUID id,
    Instant createdAt,
    UUID receiverId,
    String title,
    String content
) {

}