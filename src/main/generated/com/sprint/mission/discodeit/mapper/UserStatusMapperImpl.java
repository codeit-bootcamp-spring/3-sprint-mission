package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T09:33:36+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (OpenLogic)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusResponse toResponse(UserStatus status) {
        if ( status == null ) {
            return null;
        }

        UUID id = null;
        Instant lastActiveAt = null;

        id = status.getId();
        lastActiveAt = status.getLastActiveAt();

        UUID userId = status.getUser().getId();

        UserStatusResponse userStatusResponse = new UserStatusResponse( id, userId, lastActiveAt );

        return userStatusResponse;
    }
}
