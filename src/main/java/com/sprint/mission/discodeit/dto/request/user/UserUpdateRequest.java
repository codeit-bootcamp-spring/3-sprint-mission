package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;
import java.util.Objects;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

    public UserUpdateRequest {
        // null 체크 제거 - 부분 업데이트 허용
    }

}
