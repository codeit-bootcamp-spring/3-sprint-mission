package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class UserStatusMapper {

    public UserStatusDto toDto(UserStatus userStatus) {
        UUID id = userStatus.getId();
        UUID userId = userStatus.getUser().getId();
        Instant lastActiveAt = userStatus.getLastActiveAt();

        return new UserStatusDto(id, userId, lastActiveAt);
    }
}
