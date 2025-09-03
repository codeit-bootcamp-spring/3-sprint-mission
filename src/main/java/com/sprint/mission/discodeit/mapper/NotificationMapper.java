package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface NotificationMapper {

    @Mapping(target = "receiverId", source = "user.id")
    NotificationDto toDto(Notification notification);
}
