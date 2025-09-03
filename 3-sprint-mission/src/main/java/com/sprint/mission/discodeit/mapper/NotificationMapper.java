package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    @Mapping(target = "receivedId", source = "receiverId")
    NotificationDto toDto(Notification notification);
    
    @Mapping(target = "receiverId", source = "receivedId")
    Notification toEntity(NotificationDto notificationDto);
}
