package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 내용 업데이트 요청 DTO")
public record MessageUpdateRequest(
        @Schema(description = "새로운 메시지 내용", example = "수정된 메시지입니다.")
        String newContent
) {
}

