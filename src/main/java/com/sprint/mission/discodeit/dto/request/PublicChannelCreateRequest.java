package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널명은 필수입니다")
    @Size(min = 1, max = 20, message = "채널명은 1자 이상 20자 이하로 입력해주세요")
    String name,

    @Size(max = 500, message = "채널 소개문은 500자 이하여야 합니다")
    String description
) {

}
