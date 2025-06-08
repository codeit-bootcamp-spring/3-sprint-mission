package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAssembler {

  private final UserMapper userMapper;
  private final UserStatusRepository userStatusRepository;

  /* online는 엔티티에 없으므로 따로 계산해서 넣어줘야함.*/
  public UserDto toDtoWithOnline(User user) {
    UserDto userDto = userMapper.toDto(user);
    Boolean online = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(null);

    userDto.setOnline(online);

    return userDto;
  }
}
