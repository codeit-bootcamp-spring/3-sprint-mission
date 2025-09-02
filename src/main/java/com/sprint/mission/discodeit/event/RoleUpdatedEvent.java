package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role;

/**
 * 사용자의 권한(Role)이 변경된 경우 발행되는 이벤트
 *
 * @param user    권한이 변경된 사용자
 * @param oldRole 기존 권한
 * @param newRole 새로운 권한
 */
public record RoleUpdatedEvent(
    User user,
    Role oldRole,
    Role newRole
) {

}
