package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "수정할 메시지 내용을 입력해주세요") String newContent
) {
}
