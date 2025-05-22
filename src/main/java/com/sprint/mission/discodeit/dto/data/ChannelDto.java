package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(

    @Schema(description = "채널 ID", example = "c3a12f6b-9d6d-4b2e-bb39-30e36e0f5569")
    UUID id,

    @Schema(description = "채널 타입 (PUBLIC 또는 PRIVATE)", example = "PUBLIC")
    ChannelType type,

    @Schema(description = "채널 이름", example = "general")
    String name,

    @Schema(description = "채널에서 사용하는 카테고리 목록", example = "[\"공지\", \"자유\"]")
    String description,

    @Schema(description = "채널에 속한 유저 ID 목록", example = "[\"a1b2c3d4-5678-90ab-cdef-1234567890ab\"]")
    List<UUID> participantIds,

    @Schema(description = "마지막 메시지 전송 시간", example = "2025-05-16T15:30:00Z")
    Instant lastMessageAt

) {

}
