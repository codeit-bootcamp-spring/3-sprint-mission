package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
    @NotBlank(message = "공개 채널명은 필수입니다")
    @Size(min = 1, max = 30, message = "채널명은 1자 이상, 30자 이하여야 합니다")
    String name,

    @Size(max = 255, message = "채널 설명은 최대 255자 이내여야 합니다")
    String description
) {


}
