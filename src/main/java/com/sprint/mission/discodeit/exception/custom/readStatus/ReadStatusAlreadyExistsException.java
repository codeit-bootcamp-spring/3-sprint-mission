package com.sprint.mission.discodeit.exception.custom.readStatus;

import com.sprint.mission.discodeit.exception.base.DuplicatedException;

public class ReadStatusAlreadyExistsException extends DuplicatedException {

    public ReadStatusAlreadyExistsException(String message) {
        super(message);
    }
}
