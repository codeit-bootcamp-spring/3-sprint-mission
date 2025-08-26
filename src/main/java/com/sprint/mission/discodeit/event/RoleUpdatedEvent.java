package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.enums.Role;
import java.util.UUID;

/**
 * 사용자의 권한(Role)이 변경된 경우 발행되는 이벤트
 *
 * @param userId      권한이 변경된 사용자 Id
 * @param updatedRole 변경된 권한명
 */
public record RoleUpdatedEvent(
    UUID userId,
    Role updatedRole
) {

}
