package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

    @NotBlank
    @Size(min = 1, max = 20)
    String name,

    @NotBlank
    String description
) { }
