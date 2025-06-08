package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    UserStatusDTO toDTO(UserStatus userStatus);
    UserStatus toEntity(UserStatusDTO dto);
}
