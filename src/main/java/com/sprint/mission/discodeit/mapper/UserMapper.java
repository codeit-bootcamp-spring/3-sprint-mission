package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  private SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(isOnline(user))")
  abstract public UserDto toDto(User user);


  protected boolean isOnline(User user) {
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    for(Object principal : allPrincipals) {
      UserDetails userDetail = (UserDetails) principal;
      if(userDetail.getUsername().equals(user.getUsername())) {
        return true;
      }
    }
    return false;
  }
}
