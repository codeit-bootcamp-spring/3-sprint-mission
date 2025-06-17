package com.sprint.mission.discodeit.exception.custom.user;

import com.sprint.mission.discodeit.exception.base.DuplicatedException;

public class EmailAlreadyExistsException extends DuplicatedException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
