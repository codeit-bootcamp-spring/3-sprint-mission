package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserStatusMapper userStatusMapper;

  public UserDto toDto(User entity) {
    return new UserDto(
        entity.getId(),
        entity.getUsername(),
        entity.getEmail(),
        binaryContentMapper.toDto(entity.getProfile()),
        userStatusMapper.isOnline(entity.getStatus())
    );
  }

  public UserResponse toResponse(User entity) {
    return new UserResponse(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getUsername(),
        entity.getEmail(),
        entity.getProfile() != null ? entity.getProfile().getId() : null,
        userStatusMapper.isOnline(entity.getStatus())
    );
  }
}
