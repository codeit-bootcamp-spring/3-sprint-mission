package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자의 권한 역할이 변경되었을 때 발생하는 이벤트입니다.
 * 
 * <p>사용자의 권한이 업데이트되었을 때 로깅이나 감사 추적 등의
 * 후속 작업을 위해 이벤트 리스너에게 알리는 데 사용됩니다.</p>
 * 
 * @param userId 권한이 변경된 사용자 엔티티 ID
 * @param oldRole 변경 전 권한 역할
 * @param newRole 변경 후 권한 역할
 * @param occurredAt 이벤트 발생 시간
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
public record RoleUpdatedEvent(
        UUID userId,
        Role oldRole,
        Role newRole,
        Instant occurredAt
) {
}
