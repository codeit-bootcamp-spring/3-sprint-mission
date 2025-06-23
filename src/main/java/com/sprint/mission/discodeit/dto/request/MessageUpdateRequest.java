package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Message 수정 요청 DTO")
public record MessageUpdateRequest(
        @NotBlank String newContent) {

}
