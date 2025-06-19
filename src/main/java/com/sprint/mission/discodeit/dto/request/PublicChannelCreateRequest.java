package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank
    @Size(min = 1, max = 20, message = "채널 이름은 1자 이상, 20자 이하여야 합니다.")
    String name,
    String description
) {

}