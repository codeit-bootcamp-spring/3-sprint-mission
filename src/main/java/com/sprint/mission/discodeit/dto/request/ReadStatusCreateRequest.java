package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @Schema(description = "유저 ID", example = "9ac17d8d-052e-423e-8eb7-40be2db7cee1")
    UUID userId,

    @Schema(description = "채널 ID", example = "836ce99e-c728-48c6-8589-cdc3a693378d")
    UUID channelId,

    @Schema(description = "마지막으로 읽은 시간", example = "2025-05-15T08:25:08.207975700Z")
    Instant lastReadAt
) {

}
