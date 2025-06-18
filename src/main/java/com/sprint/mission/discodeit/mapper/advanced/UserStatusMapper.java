package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : AdvancedUserStatusResponse
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    @Mapping(source = "user.id", target = "userId")
    JpaUserStatusResponse toDto(UserStatus userStatus);
}
