package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Message 생성 요청 DTO")
public record MessageCreateRequest(
        @NotBlank String content,
        @NotNull UUID authorId,
        @NotNull UUID channelId) {

}