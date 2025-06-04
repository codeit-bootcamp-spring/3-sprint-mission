package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Mapping(target = "online", expression = "java(user.getUserStatus().isOnline())")
    public abstract UserDto toDto(User user);
//    private final BinaryContentMapper binaryContentMapper;

//    public UserDto toDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getUsername(),
//                user.getEmail(),
//                binaryContentMapper.toDto(user.getProfile()),
//                user.getUserStatus().isOnline()
//        );
//    }
}
