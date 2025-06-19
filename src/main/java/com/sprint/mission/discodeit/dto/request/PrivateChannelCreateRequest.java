package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "비공개 채널 생성 요청 DTO")
public record PrivateChannelCreateRequest(
        @NotNull @Size(min = 2) List<UUID> participantIds) {

}
