package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "변경할 사용자 정보")
public record UserUpdateRequest(
    @Schema(description = "변경할 사용자 이름")
    String newUsername,

    @Schema(description = "변경할 사용자의 이메일", type = "string", format = "email")
    String newEmail,

    @Schema(description = "변경할 사용자의 비밀번호", type = "string", format = "password")
    String newPassword
) {

}
