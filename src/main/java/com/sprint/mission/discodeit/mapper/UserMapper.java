package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected BinaryContentMapper binaryContentMapper;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDto toDto(User user);

    protected boolean isOnline(User user) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(p -> p instanceof DiscodeitUserDetails)
                .map(p -> (DiscodeitUserDetails) p)
                .anyMatch(details -> details.getUsername().equals(user.getUsername())
                        && sessionRegistry.getAllSessions(details, false).stream()
                        .anyMatch(session -> !session.isExpired()));
    }
}
