package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;


@Schema(description = "생성할 사용자 온라인 정보")
public record UserStatusCreateRequest(
    @Schema(description = "사용자 상태 정보를 생성할 사용자 ID", type = "string", format = "uuid")
    UUID userId,

    @Schema(description = "생성할 사용자 대상의 마지막 온라인 시간 정보", type = "string", format = "date-time")
    Instant lastActiveAt
) {

}
