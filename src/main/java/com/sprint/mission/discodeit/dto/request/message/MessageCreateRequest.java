package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank String content,
    @NotBlank UUID channelId,
    @NotBlank UUID authorId
) {
    public MessageCreateRequest {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(channelId, "channelId must not be null");
        Objects.requireNonNull(authorId, "authorId must not be null");
    }
}
