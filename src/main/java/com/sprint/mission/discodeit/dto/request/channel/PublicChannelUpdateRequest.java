package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record PublicChannelUpdateRequest(
    @NotBlank String newName,
    @NotBlank String newDescription
) {
    public PublicChannelUpdateRequest {
        Objects.requireNonNull(newName, "newName must not be null");
        Objects.requireNonNull(newDescription, "newDescription must not be null");
    }
}
