package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "사용자 상태 생성 요청 DTO")
public record UserStatusCreateRequest(UUID userId) {

}
