package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "비공개 채널 생성 요청 DTO")
public record PrivateChannelCreateRequest(
        @Schema(
                description = "채널 참가자들의 ID 목록",
                type = "array",
                example = "[\"123e4567-e89b-12d3-a456-426614174000\", \"987fcdeb-51a2-43d8-9876-543210abcdef\"]"
        )
        @ArraySchema(schema = @Schema(type = "string", format = "uuid"))
        List<UUID> participantIds
) {}
