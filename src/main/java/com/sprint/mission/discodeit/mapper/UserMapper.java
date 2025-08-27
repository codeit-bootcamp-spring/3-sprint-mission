package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  // 기존 메소드 ( online 상태 없이 )
  @Mapping(target = "online", ignore = true)
  UserDto toDto(User user);

  // 새로운 메소드 - online 상태를 직접 설정
  default UserDto toDto(User user, boolean online) {
    UserDto baseDto = toDto(user);
    return new UserDto(
        baseDto.id(),
        baseDto.username(),
        baseDto.email(),
        baseDto.role(),
        baseDto.profile(),
        online
    );
  }
}
