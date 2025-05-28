package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Message 수정 요청 DTO")
public record MessageUpdateRequest(
    String newContent
) {

}
