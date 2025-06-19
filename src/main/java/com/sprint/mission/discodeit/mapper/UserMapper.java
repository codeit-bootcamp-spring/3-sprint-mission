package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        UUID id = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        BinaryContentDto profile = Optional.ofNullable(user.getProfile())
                .map(binaryContentMapper::toDto)
                .orElse(null);

        Boolean online = Optional.ofNullable(user.getStatus())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                id,
                username,
                email,
                profile,
                online);
    }
}