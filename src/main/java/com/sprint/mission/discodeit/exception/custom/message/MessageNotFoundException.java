package com.sprint.mission.discodeit.exception.custom.message;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class MessageNotFoundException extends NotFoundException {

    public MessageNotFoundException(String message) {
        super(message);
    }
}
