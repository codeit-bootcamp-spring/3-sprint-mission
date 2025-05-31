package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.entity.User;
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

    BinaryContentMapper binaryContentMapper;

    public UserDto toDto(User user) {
        UUID id = user.getId();
        String email = user.getEmail();
        String password = user.getPassword();
        BinaryContentDto profile = binaryContentMapper.toDto(user.getProfile());
        // FIXME
        Boolean online = user.getStatus().isOnline();

        return new UserDto(id, email, password, profile, online);
    }

}
