package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record MessageUpdateRequest(
    @NotBlank String newContent
) {
    public MessageUpdateRequest {
        Objects.requireNonNull(newContent, "newContent must not be null");
    }
}
