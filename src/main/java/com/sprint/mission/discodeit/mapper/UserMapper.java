package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;

    public UserResponseDto toDto(User user) {
        BinaryContentResponseDto profileDto = null;

        if (user.getProfile() != null) {
            profileDto = binaryContentMapper.toDto(user.getProfile());
        }

        String username = user.getUsername();

        return new UserResponseDto(user.getId(), username, user.getEmail(),
            profileDto, true, user.getRole());
    }
}
