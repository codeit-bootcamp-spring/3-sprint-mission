package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름은 필수 입력 값입니다.")
    String name,
    String description
) {

}
