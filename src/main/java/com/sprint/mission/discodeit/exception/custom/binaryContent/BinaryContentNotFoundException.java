package com.sprint.mission.discodeit.exception.custom.binaryContent;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class BinaryContentNotFoundException extends NotFoundException {

    public BinaryContentNotFoundException(String message) {
        super(message);
    }
}
