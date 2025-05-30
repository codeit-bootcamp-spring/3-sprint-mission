package com.sprint.mission.discodeit.mapper.struct;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusStructMapper {

    @Mapping(target = "userId", source = "user.id")
    UserStatusResponseDto toDto(UserStatus userStatus);
}
