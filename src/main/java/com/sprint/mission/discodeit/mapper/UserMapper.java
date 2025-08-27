package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final JwtRegistry jwtRegistry;

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        boolean isOnline = jwtRegistry.hasActiveJwtInformationByUserId(user.getId());

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            binaryContentMapper.toDto(user.getProfile()),
            isOnline,
            user.getRole()
        );
    }
}