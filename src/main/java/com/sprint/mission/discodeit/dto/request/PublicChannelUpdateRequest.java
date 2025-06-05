package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 수정 요청 DTO")
public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
