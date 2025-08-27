package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = { BinaryContentMapper.class, OnlineStatusResolver.class })
public abstract class UserMapper {

    @Autowired
    protected OnlineStatusResolver onlineStatusResolver;

    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "online", expression = "java(onlineStatusResolver.resolve(user.getId()))")
    public abstract UserDto toDto(User user);
}