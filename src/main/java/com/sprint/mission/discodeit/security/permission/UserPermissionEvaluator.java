package com.sprint.mission.discodeit.security.permission;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("userPermissionEvaluator")
public class UserPermissionEvaluator {
    public boolean isSelf(UUID targetUserId, UUID currentUserId) {
        return targetUserId != null && targetUserId.equals(currentUserId);
    }

}
