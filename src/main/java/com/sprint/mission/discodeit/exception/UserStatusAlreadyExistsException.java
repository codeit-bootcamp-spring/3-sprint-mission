package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.entity.UserStatus;

public class UserStatusAlreadyExistsException extends RuntimeException {

    public UserStatusAlreadyExistsException(UserStatus userStatus) {
        super("해당하는 유저의 status가 이미 존재합니다. userStatus 정보 : " + userStatus.toString());
    }
}
