package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  @Mapping(target = "online", qualifiedByName = "getIsOnline")
  public abstract UserDto toDto(User user);

  //  @Mapping(target = "online", ignore = true)
  public abstract User toEntity(UserDto userDto);

  @Named("getIsOnline")
  default boolean getIsOnline(User user) {
    return user.getStatus().isOnline();
  }

}
