package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : AdvancedUserMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */

//@RequiredArgsConstructor
@Mapper(uses = {BinaryContentMapper.class}, componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private JwtRegistry jwtRegistry;

    @Mapping(source = "profile", target = "profile")
    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDto toDto(User user);

    protected boolean isOnline(User user) {
        return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
    }

}
