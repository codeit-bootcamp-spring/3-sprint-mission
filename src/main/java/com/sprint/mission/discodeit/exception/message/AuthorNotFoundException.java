package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class AuthorNotFoundException extends MessageException {

    public AuthorNotFoundException(UUID authorId) {
        super(ErrorCode.AUTHOR_NOT_FOUND, Map.of("authorId", authorId));
    }
}