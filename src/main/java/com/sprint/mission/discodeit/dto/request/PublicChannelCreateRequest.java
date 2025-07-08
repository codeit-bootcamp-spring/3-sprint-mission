package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름은 필수 입력 값입니다.")
    @Size(max = 100)
    String name,

    @Size(max = 500)
    String description
) {

}
