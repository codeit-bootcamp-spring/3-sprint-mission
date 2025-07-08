package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreateRequest(
        String content,
        @NotNull(message = "채널 ID를 입력해주세요") UUID channelId,
        @NotNull(message = "작성자 ID를 입력해주세요") UUID authorId
) {
}
