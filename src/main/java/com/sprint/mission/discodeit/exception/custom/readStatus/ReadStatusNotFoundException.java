package com.sprint.mission.discodeit.exception.custom.readStatus;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class ReadStatusNotFoundException extends NotFoundException {

    public ReadStatusNotFoundException(String message) {
        super(message);
    }
}
