package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

/* TODO : UserCreateResponse record 랑 합치기 -> 불가능 password 정보 제외해야함 */
public record UserResponse(UUID userId, String name, String email, UUID profileId,
                           UserStatus userStatus) {
}
