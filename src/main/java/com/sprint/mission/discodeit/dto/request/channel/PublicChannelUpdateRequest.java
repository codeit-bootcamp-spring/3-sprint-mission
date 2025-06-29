package com.sprint.mission.discodeit.dto.request.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "수정할 채널명을 입력해주세요") String newName,
    @NotBlank(message = "수정할 채널 설명을 입력해주세요") String newDescription
) {
}
