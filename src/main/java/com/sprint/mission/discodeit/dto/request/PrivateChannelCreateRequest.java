package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Schema(description = "비공개 채널 생성 요청 DTO")
public record PrivateChannelCreateRequest(
    @NotNull @NotEmpty List<UUID> participantIds
) {

}
