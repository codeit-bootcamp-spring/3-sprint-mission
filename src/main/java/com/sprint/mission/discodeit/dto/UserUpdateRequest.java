package com.sprint.mission.discodeit.dto;

import java.util.UUID;

//수정 대상 객체의 id 파라미터, 수정할 값 파라미터
public record UserUpdateRequest(UUID userId, String newName, String newEmail, String newPassword, UUID newProfileId) {
}

