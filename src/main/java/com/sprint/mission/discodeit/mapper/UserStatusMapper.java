package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserStatusMapper {

  @Mapping(target = "userId", source = "userStatus.user.id")
  UserStatusDto toDto(UserStatus userStatus);

  UserStatus toEntity(UserStatusDto userStatusDto);

}
