package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserEmailDuplicationException extends UserException {

    public UserEmailDuplicationException(String email) {
        super(ErrorCode.DUPLICATE_USER_EMAIL, Map.of("가입하려고 한 이메일", email));
    }
}

