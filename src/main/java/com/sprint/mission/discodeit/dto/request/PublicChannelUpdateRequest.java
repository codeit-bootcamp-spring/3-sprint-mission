package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "채널 수정 요청 DTO")
public record PublicChannelUpdateRequest(
        @NotBlank @Size(max = 50) String newName,
        @NotBlank @Size(max = 255) String newDescription) {

}
