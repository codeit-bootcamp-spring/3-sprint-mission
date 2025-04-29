package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.entity.ReadStatus;

public class ReadStatusAlreadyExistsException extends RuntimeException {

    public ReadStatusAlreadyExistsException(ReadStatus readStatus) {
        super("해당하는 유저,채널의 readStatus가 이미 존재합니다. readStatus 정보 : " + readStatus.toString());
    }
}
