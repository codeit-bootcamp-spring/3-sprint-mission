package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record PublicChannelCreateRequest(
    @NotBlank String name,
    @NotBlank String description
) {
    public PublicChannelCreateRequest {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(description, "description must not be null");
    }
}
