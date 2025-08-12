package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.basic.SessionStatusService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected SessionStatusService sessionStatusService;

    @Mapping(target = "online", expression = "java(sessionStatusService.isUserLoggedIn(user.getId()))")
    public abstract UserDto toDto(User user);
}
