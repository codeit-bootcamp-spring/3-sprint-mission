package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank @Size(max = 100)
    String newName,

    @Size(max = 500)
    String newDescription
) {

}
