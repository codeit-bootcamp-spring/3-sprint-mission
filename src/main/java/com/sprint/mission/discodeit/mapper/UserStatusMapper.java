package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
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
        
        // 프록시 객체 안전 접근
        UUID userId = null;
        try {
            userId = userStatus.getUser().getId();
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }
        
        Instant lastActiveAt = userStatus.getLastActiveAt();

        return new UserStatusDto(id, userId, lastActiveAt);
    }
}
