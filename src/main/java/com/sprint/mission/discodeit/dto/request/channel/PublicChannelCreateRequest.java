package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequest(
    @NotBlank(message = "채널명을 입력해주세요") String name,
    @NotBlank(message = "채널 설명을 입력해주세요") String description
) {
}
