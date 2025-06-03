package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        BinaryContentDto profile = Optional.ofNullable(user.getProfile())
            .map(binaryContentMapper::toDto)
            .orElse(null);

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            profile,
            user.getStatus().isOnline()
        );
    }
}
