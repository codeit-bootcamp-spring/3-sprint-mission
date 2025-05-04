package com.sprint.mission.discodeit.entity.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public record UpdateUserRequest(
        String username,    // 사용자가 수정할 수 있는 필드들만 포함
        String email,
        String password,
        Optional<BinaryContent> binaryContent) {
}
