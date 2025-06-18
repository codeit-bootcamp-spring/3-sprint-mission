package com.sprint.mission.discodeit.mapper.legacy;

import com.sprint.mission.discodeit.dto.user.JpaUserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.advanced.BinaryContentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : UserMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */

@RequiredArgsConstructor
@Component
public class OldUserMapper {
//    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentMapper binaryContentMapper;

    public JpaUserResponse toDto(User user) {
        if(user == null) return null;

        return JpaUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(binaryContentMapper.toDto(user.getProfile()))
                .online(isOnline(user.getStatus()))
                .build();
    }

    private static boolean isOnline(UserStatus userStatus) {
        Instant now = Instant.now();
        return Duration.between(userStatus.getLastActiveAt(), now).toMinutes() < 5;
    }
}
