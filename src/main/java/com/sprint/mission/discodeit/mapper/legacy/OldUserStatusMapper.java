package com.sprint.mission.discodeit.mapper.legacy;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : UserStatusMapper
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
@Component
public class OldUserStatusMapper {
    public JpaUserStatusResponse toDto(UserStatus userStatus) {
        if(userStatus == null) return null;

        return JpaUserStatusResponse.builder()
                .id(userStatus.getId())
                .userId(userStatus.getUser().getId())
                .lastActiveAt(userStatus.getLastActiveAt())
                .build();
    }
}
