package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class UserNotFoundByNameException extends UserException {

    public UserNotFoundByNameException(String userName) {
        super(ErrorCode.USER_NOT_FOUND_BY_NAME, Map.of("조회하려고 한 유저 이름: ", userName));
    }
}
