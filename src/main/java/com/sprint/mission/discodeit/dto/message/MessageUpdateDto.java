package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.Size;

public record MessageUpdateDto(
        @Size(max = 1000, message = "메시지는 1000자 이내여야 합니다.")
        String newContent) {

}
