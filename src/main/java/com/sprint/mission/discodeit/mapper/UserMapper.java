package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.io.IOException;
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
        String email = user.getEmail();
        String password = user.getPassword();
        BinaryContentDto profile = null;
        try {
            if (user.getProfile() != null) {
                profile = binaryContentMapper.toDto(user.getProfile());
            }
        } catch (IOException e) {
            // 로깅하거나 fallback 처리
            throw new RuntimeException(e);
        }

        // FIXME
        Boolean online = user.getStatus().isOnline();

        return new UserDto(id, email, password, profile, online);
    }
}
