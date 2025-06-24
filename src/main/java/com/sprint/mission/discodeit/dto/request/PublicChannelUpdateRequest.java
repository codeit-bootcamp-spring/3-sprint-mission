package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 업데이트 요청 DTO")
public record PublicChannelUpdateRequest(
        @Schema(description = "새로운 채널 이름", example = "새로운-채널명", nullable = true)
        String newName,

        @Schema(description = "새로운 채널 설명", example = "채널 설명이 수정되었습니다", nullable = true)
        String newDescription
) {
}
