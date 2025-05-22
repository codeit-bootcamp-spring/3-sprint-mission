package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelUpdateRequest(
    @Schema(description = "새로운 채널 이름", example = "업데이트된 채널명")
    String newName,

    @Schema(description = "새로 설정할 카테고리 목록", example = "[\"공지\", \"자유\"]")
    String newDescription
) {

}
