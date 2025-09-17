package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public class UserEvent extends BaseEvent<User> {

    public UserEvent(UUID id, User user, EventType type) {
        super(id, user, type);
    }
}