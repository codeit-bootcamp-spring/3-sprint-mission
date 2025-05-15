package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.entity.User;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(User user) {
        super("이미 존재하는 회원입니다. 유저정보 : " + user.toString());
    }
}
