package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @Size(min = 1, max = 30, message = "공개 채널명은 1자 이상, 30자 이하여야 합니다")
    String newName,

    @Size(max = 255, message = "공개 채널의 설명은 최대 255자 이내여야 합니다")
    String newDescription
) {


}
