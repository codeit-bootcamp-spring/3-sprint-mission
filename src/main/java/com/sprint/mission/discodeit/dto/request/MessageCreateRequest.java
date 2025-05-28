package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
@Schema(description = "메시지 생성 요청 DTO")
public record MessageCreateRequest(
        @Schema(description = "메시지 내용", example = "안녕하세요!")
        String content,

        @Schema(description = "메시지가 속한 채널 ID", type = "string", format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID channelId,

        @Schema(description = "메시지 작성자 ID", type = "string", format = "uuid",
                example = "123e4567-e89b-12d3-a456-426614174000")
        UUID authorId
) {}
