package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNameDuplicationException extends UserException {

    public UserNameDuplicationException(String userName) {
        super(ErrorCode.DUPLICATE_USER_NAME, Map.of("가입하려고 한 이름", userName));
    }
}
