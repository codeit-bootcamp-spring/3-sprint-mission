package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : NotificationMapper
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);
}


