package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "채널 정보를 담는 DTO")
public record ChannelDto(
    // 채널 정보
    @Schema(description = "채널 ID", type = "string", format = "uuid")
    UUID id,

    @Schema(description = "채널 유형", example = "PUBLIC")
    ChannelType type,

    @Schema(description = "채널 이름", example = "개발자들의 쉼터", type = "string", format = "string")
    String name,

    @Schema(description = "채널 설명", example = "개발자들의 소통 커뮤니티입니다")
    String description,

    @Schema(description = "채널 참가자의 ID 목록", type = "string", format = "uuid")
    List<UUID> participantIds,

    @Schema(description = "마지막 메세지 시간", type = "string", format = "date-time")
    Instant lastMessageAt
) {

}
