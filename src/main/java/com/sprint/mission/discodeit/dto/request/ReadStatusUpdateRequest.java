package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "읽음 상태 업데이트 요청 DTO")
public record ReadStatusUpdateRequest(
        @Schema(description = "새로운 마지막 읽은 시간", type = "string", format = "date-time",
                example = "2024-03-20T09:12:28Z")
        Instant newLastReadAt
) {}
