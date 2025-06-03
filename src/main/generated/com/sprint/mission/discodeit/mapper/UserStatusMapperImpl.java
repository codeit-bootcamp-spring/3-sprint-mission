package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T09:31:51+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusDto toDto(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UserStatusDto.UserStatusDtoBuilder userStatusDto = UserStatusDto.builder();

        userStatusDto.id( userStatus.getId() );
        userStatusDto.lastActiveAt( userStatus.getLastActiveAt() );

        userStatusDto.userId( userStatus.getUser().getId() );

        return userStatusDto.build();
    }
}
