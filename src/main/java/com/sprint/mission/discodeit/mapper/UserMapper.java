package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserStatusRepository userStatusRepository;

    public UserDto toDto(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(status -> status.getUpdatedAt().isAfter(Instant.now().minusSeconds(300)))
                .orElse(false);

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(binaryContentMapper.toDto(user.getProfile()))
                .online(isOnline)
                .build();
    }
}