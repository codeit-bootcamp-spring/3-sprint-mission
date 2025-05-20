package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
        name = "ChannelDto",
        description = "채널 정보를 전달하기 위한 Data Transfer Object"
)
public record ChannelDto(
        @Schema(
                description = "채널의 고유 식별자",
                type = "string",
                format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
                description = "채널 유형 (PUBLIC, PRIVATE 등)",
                type = "string",
                example = "PUBLIC"
        )
        ChannelType type,

        @Schema(
                description = "채널 이름",
                type = "string",
                example = "일반-채팅방",
                minLength = 1,
                maxLength = 100
        )
        String name,

        @Schema(
                description = "채널 설명",
                type = "string",
                example = "일반적인 대화를 나누는 채널입니다",
                nullable = true
        )
        String description,

        @ArraySchema(
                schema = @Schema(description = "채널 참여자들의 ID 목록"),
                arraySchema = @Schema(
                        type = "string",
                        format = "uuid",
                        example = "123e4567-e89b-12d3-a456-426614174000",
                        description = "채널에 참여한 사용자의 고유 식별자"
                )
        )
        List<UUID> participantIds,

        @Schema(
                description = "채널에서 마지막 메시지가 전송된 시간",
                type = "string",
                format = "date-time",
                example = "2024-03-20T09:12:28Z",
                nullable = true
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant lastMessageAt
) {}

