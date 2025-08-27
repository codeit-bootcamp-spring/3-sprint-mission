package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper.advanced
 * FileName     : AdvancedUserMapper
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */

//@RequiredArgsConstructor
@Mapper(uses = {BinaryContentMapper.class}, componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Mapping(source = "profile", target = "profile")
    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserResponse toDto(User user);

    protected boolean isOnline(User user){
        return sessionRegistry.getAllPrincipals().stream()
            .filter(p -> p instanceof DiscodeitUserDetails)
            .map(p -> (DiscodeitUserDetails)p)
            .anyMatch(d -> d.getUser().id().equals(user.getId()));
    }
}
