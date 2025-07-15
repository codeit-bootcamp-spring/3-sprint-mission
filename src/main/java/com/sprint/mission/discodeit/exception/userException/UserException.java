package com.sprint.mission.discodeit.exception.userException;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : UserException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */
public class UserException extends DiscodeitException {
    public UserException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
