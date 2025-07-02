package com.sprint.mission.discodeit.dto.Message;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(

        @NotBlank(message = "메시지 내용은 빈 값일 수 없습니다.")
        String newContent
) {

}
