package com.sprint.mission.discodeit.exception.custom.userStatus;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class UserStatusNotFoundException extends NotFoundException {

    public UserStatusNotFoundException(String message) {
        super(message);
    }
}
