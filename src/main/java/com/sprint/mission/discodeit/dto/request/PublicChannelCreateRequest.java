package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널 이름을 입력해주세요.")
    @Size(min = 2, max = 50, message = "채널 이름은 2자 이상 50자 이하로 입력해주세요.")
    String name,

    @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
    String description
) {

}
