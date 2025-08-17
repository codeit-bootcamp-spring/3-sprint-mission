package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {BinaryContentMapper.class}
)
public abstract class UserMapper {

    @Autowired
    protected AuthService authService;

    @Mapping(target = "isOnline", expression = "java(authService.isUserOnline(user.getId()))")
    public abstract UserDto toDto(User user);
}