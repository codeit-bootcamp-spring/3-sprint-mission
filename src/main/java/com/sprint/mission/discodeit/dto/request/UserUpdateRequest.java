package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "사용자 수정 요청 DTO")
public record UserUpdateRequest(
    String name,
    String password,
    UUID profileImageId
) {

}