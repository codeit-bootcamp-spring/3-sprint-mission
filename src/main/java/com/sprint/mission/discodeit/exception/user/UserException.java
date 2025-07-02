
package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public abstract class UserException extends DiscodeitException {
    protected UserException(ErrorCode errorCode, String details) {
        super(errorCode, details);
    }
}
