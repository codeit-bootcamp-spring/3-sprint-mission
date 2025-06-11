package com.sprint.mission.discodeit.mapper.advanced;

import com.sprint.mission.discodeit.dto.userStatus.JpaUserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.mapper
 * FileName     : AdvancedUserStatusResponse
 * Author       : dounguk
 * Date         : 2025. 6. 3.
 */
@Mapper(componentModel = "spring")
public interface AdvancedUserStatusMapper {
    AdvancedUserStatusMapper INSTANCE = Mappers.getMapper(AdvancedUserStatusMapper.class);

    @Mapping(source = "user.id", target = "userId")
    JpaUserStatusResponse toDto(UserStatus userStatus);
}
