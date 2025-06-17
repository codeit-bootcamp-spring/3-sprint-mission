package com.sprint.mission.discodeit.exception.custom.user;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
