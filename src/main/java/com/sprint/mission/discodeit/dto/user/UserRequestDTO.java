package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User 생성 및 수정 정보")
public record UserRequestDTO(String username, String email, String password, String introduction) {

  public static User toEntity(UserRequestDTO userRequestDTO) {
    User user = new User(userRequestDTO.username(), userRequestDTO.email(),
        userRequestDTO.password(), userRequestDTO.introduction());

    return user;
  }
}
