package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(min = 1, max = 100)
    String newName,

    @Size(max = 1000)
    String newDescription
) {

}
