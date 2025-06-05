package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공개 채널 생성 요청 DTO")
public record PublicChannelCreateRequest(
    String name,
    String description
) {

}
