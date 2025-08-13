package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @NotBlank
    @Size(min = 1, max = 20)
    String newName,

    @NotBlank
    String newDescription
) { }