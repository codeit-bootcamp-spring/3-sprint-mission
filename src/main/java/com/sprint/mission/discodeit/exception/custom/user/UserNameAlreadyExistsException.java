package com.sprint.mission.discodeit.exception.custom.user;

import com.sprint.mission.discodeit.exception.base.DuplicatedException;

public class UserNameAlreadyExistsException extends DuplicatedException {

    public UserNameAlreadyExistsException(String message) {
        super(message);
    }
}
