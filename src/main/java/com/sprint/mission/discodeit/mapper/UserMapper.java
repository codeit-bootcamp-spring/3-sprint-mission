package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

  @Autowired
  UserStatusRepository userStatusRepository;


  public abstract UserDto toDto(User user);

  //  @Mapping(target = "online", ignore = true)
  public abstract User userDtoToUser(UserDto userDto);

}
