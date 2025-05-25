package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "변경할 메세지 정보")
public record MessageUpdateRequest(
    @Schema(description = "변경할 메세지 내용", example = "changeText")
    String newContent
) {

}
