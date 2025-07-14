package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotNull
    UUID authorId,
    @NotNull
    UUID channelId,
    @NotBlank(message = "메시지 내용은 필수 입력값입니다.")
    String content
) {

}
