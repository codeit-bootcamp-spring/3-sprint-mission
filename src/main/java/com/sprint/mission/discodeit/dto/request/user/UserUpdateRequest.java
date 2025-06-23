package com.sprint.mission.discodeit.dto.request.user;

import jakarta.validation.constraints.Email;

public record UserUpdateRequest(
        String newUsername,
        @Email(message = "올바른 이메일 형식으로 입력해주세요.") String newEmail,
        String newPassword
) {}
