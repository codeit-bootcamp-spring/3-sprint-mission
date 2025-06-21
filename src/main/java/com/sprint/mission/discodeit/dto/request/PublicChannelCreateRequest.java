package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "공개 채널 생성 요청 DTO")
public record PublicChannelCreateRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 255) String description) {

}
