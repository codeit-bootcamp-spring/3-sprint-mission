package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.SessionUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

    @Mapping(target = "online",
        expression = "java(sessionUtils.isUserLoggedIn(user.getId()))")
    @Mapping(source = "profile", target = "profile")
    UserDto toDto(User user, @Context SessionUtils sessionUtils);
}
