package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "사용자 상태 수정 요청 DTO")
public record UserStatusUpdateRequest(
    UUID userId
) {

}
