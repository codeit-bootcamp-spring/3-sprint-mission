package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PublicChannelCreateRequest(
    @Schema(description = "생성할 채널 이름", example = "general")
    String name,

    @Schema(description = "채널에서 사용할 메시지 카테고리 목록", example = "[\"공지\", \"자유\", \"Q&A\"]")
    String description
) {

}
