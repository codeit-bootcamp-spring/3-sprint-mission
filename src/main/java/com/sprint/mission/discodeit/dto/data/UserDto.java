package com.sprint.mission.discodeit.dto.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record UserDto(

    @Schema(description = "유저 ID", example = "d21efb88-7c3a-4dc5-8de7-4cfa3d9bcb1f")
    UUID id,

    @Schema(description = "유저 생성 시간", example = "2025-05-15T13:00:00Z")
    Instant createdAt,

    @Schema(description = "유저 정보 최종 수정 시간", example = "2025-05-16T09:20:00Z")
    Instant updatedAt,

    @Schema(description = "유저 이름", example = "조현아")
    String username,

    @Schema(description = "유저 이메일", example = "hyuna@example.com")
    String email,

    @Schema(description = "프로필 이미지 ID", example = "b3f52d9d-6ea4-48c4-8415-eed451a3b3f2")
    UUID profileId,

    @Schema(description = "유저 온라인 상태", example = "true")
    Boolean online
) {

}
