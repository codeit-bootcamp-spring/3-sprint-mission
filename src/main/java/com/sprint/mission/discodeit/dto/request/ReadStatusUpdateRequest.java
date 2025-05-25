package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "변경될 메세지 읽음 상태 정보")
public record ReadStatusUpdateRequest(
    @Schema(description = "변경될 마지막 메세지 읽음 정보 시간", type = "string", format = "date-time")
    Instant newLastReadAt
) {

}
