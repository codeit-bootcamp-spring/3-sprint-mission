package com.sprint.mission.discodeit.exception.custom.userStatus;

import com.sprint.mission.discodeit.exception.base.DuplicatedException;

public class UserStatusAlreadyExistsException extends DuplicatedException {

    public UserStatusAlreadyExistsException(String message) {
        super(message);
    }
}
