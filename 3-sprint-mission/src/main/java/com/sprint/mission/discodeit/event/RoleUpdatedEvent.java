package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.time.Instant;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,           // 권한이 변경된 사용자 ID
    String username,       // 사용자명 (로그/알림용)
    String email,          // 이메일 (알림용)
    Role previousRole,     // 이전 권한
    Role newRole,          // 새로운 권한
    Instant updatedAt      // 권한 변경 시각
) {

}
