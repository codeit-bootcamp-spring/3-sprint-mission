package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder /* 빌더 패턴을 사용해봄.
장점 1. 요구사항에 따른 필요 데이터만 사용가능
장점 2. 새로운 필드 추가시 DTO의 구조를 쉽게 수정하여 유연성 및 확장성 UP
장점 3. 필드 설정이 명확하고, 코드가 깔끔해져서 가독성 UP
*/
public record UserDTO(UUID id,
                      Instant cratedAt,
                      Instant updatedAt,
                      String username,
                      String email,
                      UUID profileId,
                      boolean online) {

  public static UserDTO fromDomain(User user, boolean isOnline) {
    return UserDTO.builder()
        .id(user.getId())
        .cratedAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .username(user.getUsername())
        .email(user.getEmail())
        .profileId(user.getProfileId())
        .online(isOnline)
        .build();

  }
}
