package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "User 온라인 상태 생성 정보")
public record UserStatusRequestDTO(UUID userId, Instant lastActiveAt) {

//  public static UserStatus toEntity(UserStatusRequestDTO userStatusRequestDTO) {
//    UserStatus userStatus = new UserStatus(userStatusRequestDTO.userId(),
//        userStatusRequestDTO.lastActiveAt());
//
//    return userStatus;
//  }
}
