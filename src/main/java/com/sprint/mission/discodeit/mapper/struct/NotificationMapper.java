package com.sprint.mission.discodeit.mapper.struct;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "receiverId", source = "user.id")
    NotificationDto toDto(Notification notification);
}
