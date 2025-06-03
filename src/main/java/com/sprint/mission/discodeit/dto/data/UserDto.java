package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "사용자 정보를 담는 DTO")
public record UserDto(
    @Schema(description = "사용자 ID", type = "string", format = "uuid")
    UUID id,

    @Schema(description = "사용자 정보가 생성된 시각", type = "string", format = "date-time")
    Instant createdAt,

    @Schema(description = "사용자 정보가 수정된 시각", type = "string", format = "date-time")
    Instant updateAt,

    @Schema(description = "사용자 이름", example = "홍길동")
    String username,

    @Schema(description = "사용자 이메일", example = "example@test.com", type = "string", format = "email")
    String email,

    @Schema(description = "프로필 이미지 파일 ID", type = "string", format = "uuid")
    UUID profileId,

    @Schema(description = "사용자의 온라인 상태", example = "온라인", type = "string", format = "boolean")
    Boolean online
) {

}
