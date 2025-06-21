package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  @Mapping(target = "online", expression = "java(user.getUserStatus().online())")
  UserDTO toDTO(User user);
}
