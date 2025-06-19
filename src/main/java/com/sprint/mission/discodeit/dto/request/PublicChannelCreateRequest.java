package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름을 입력해주세요.")
    @Size(max = 50, message = "채널 이름은 50자 이하여야 합니다.")
    String name,

    @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
    String description
) {

}
