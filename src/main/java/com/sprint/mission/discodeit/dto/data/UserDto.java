package com.sprint.mission.discodeit.dto.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(
        name = "UserDto",
        description = "사용자 정보를 전달하기 위한 Data Transfer Object"
)
public record UserDto(
        @Schema(
                description = "사용자의 고유 식별자",
                type = "string",
                format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID id,

        @Schema(
                description = "사용자 계정 생성 시간",
                type = "string",
                format = "date-time",
                example = "2024-03-20T09:12:28Z"
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant createdAt,

        @Schema(
                description = "사용자 정보 최종 수정 시간",
                type = "string",
                format = "date-time",
                example = "2024-03-20T09:12:28Z"
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant updatedAt,

        @Schema(
                description = "사용자 이름",
                type = "string",
                example = "john_doe",
                minLength = 3,
                maxLength = 50
        )
        String username,

        @Schema(
                description = "사용자 이메일 주소",
                type = "string",
                format = "email",
                example = "john.doe@example.com"
        )
        String email,

        @Schema(
                description = "사용자 프로필 이미지의 ID",
                type = "string",
                format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000",
                nullable = true
        )
        UUID profileId,

        @Schema(
                description = "사용자의 현재 온라인 상태",
                type = "boolean",
                example = "true"
        )
        Boolean online
) {}

