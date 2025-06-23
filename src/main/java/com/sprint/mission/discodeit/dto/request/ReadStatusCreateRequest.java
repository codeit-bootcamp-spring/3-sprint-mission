package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Message 읽음 상태 생성 요청 DTO")
public record ReadStatusCreateRequest(
    @NotNull UUID userId,
    @NotNull UUID channelId) {

}