package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank String content,
    @NotNull UUID channelId,
    @NotNull UUID authorId
) {

    public MessageCreateRequest {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(channelId, "channelId must not be null");
        Objects.requireNonNull(authorId, "authorId must not be null");
    }
}
