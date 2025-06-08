package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-06T12:22:44+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (JetBrains s.r.o.)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusDto toDto(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        Instant lastActiveAt = null;

        userId = userStatusUserId( userStatus );
        id = userStatus.getId();
        lastActiveAt = userStatus.getLastActiveAt();

        UserStatusDto userStatusDto = new UserStatusDto( id, userId, lastActiveAt );

        return userStatusDto;
    }

    @Override
    public UserStatus userStatusDtoToUserStatus(UserStatusDto userStatusDto) {
        if ( userStatusDto == null ) {
            return null;
        }

        UserStatus.UserStatusBuilder userStatus = UserStatus.builder();

        userStatus.id( userStatusDto.id() );
        userStatus.lastActiveAt( userStatusDto.lastActiveAt() );

        return userStatus.build();
    }

    private UUID userStatusUserId(UserStatus userStatus) {
        User user = userStatus.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
