package com.sprint.mission.discodeit.exception.ReadStatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusAlreadyExistsException extends ReadStatusException {

  public ReadStatusAlreadyExistsException() {
    super(ErrorCode.DUPLICATE_READSTATUS);
  }

  public ReadStatusAlreadyExistsException(ReadStatus readStatus) {
    super(ErrorCode.DUPLICATE_READSTATUS,
        String.format("해당하는 유저,채널의 readStatus가 이미 존재합니다: Email=%s",
            readStatus.getUser().getEmail()));
    super.addDetails("readStatus", readStatus);
  }

}
