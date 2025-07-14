package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "수정할 메시지 내용을 입력하세요.")
    String newContent
) {

}
