package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final JwtRegistry jwtRegistry;

    public UserResponseDto toDto(User user) {
        BinaryContentResponseDto profileDto = null;

        if (user.getProfile() != null) {
            profileDto = binaryContentMapper.toDto(user.getProfile());
        }

        UUID userId = user.getId();

        boolean online = isOnline(userId);

        return new UserResponseDto(userId, user.getUsername(), user.getEmail(),
            profileDto, online, user.getRole());
    }

    private boolean isOnline(UUID userId) {
        return jwtRegistry.hasActiveJwtInformationByUserId(userId);
    }
}
