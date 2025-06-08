package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserStatusRepository userStatusRepository;

    public UserDto toDto(User user) {
        if (user == null) return null;
        
        BinaryContentDto profileDto = binaryContentMapper.toDto(user.getProfile());
        Boolean online = userStatusRepository.findByUserId(user.getId())
            .map(UserStatus::isOnline)
            .orElse(null);

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            profileDto,
            online
        );
    }
}
