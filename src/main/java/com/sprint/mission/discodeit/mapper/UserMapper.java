package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.service.AuthService;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = { BinaryContentMapper.class })
public abstract class UserMapper {

    private AuthService authService;

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "online", expression = "java(computeOnline(user.getId()))")
    public abstract UserDto toDto(User user);

    protected Boolean computeOnline(UUID userId) {
        return authService.isLoggedIn(userId);
    }
}