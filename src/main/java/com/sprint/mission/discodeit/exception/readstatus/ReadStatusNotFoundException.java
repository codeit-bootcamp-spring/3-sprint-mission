package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ReadStatusNotFoundException extends ReadStatusException {
  public ReadStatusNotFoundException(String readStatusId) {
    super(ErrorCode.READ_STATUS_NOT_FOUND, "읽기 상태 정보를 찾을 수 없습니다. [ReadStatusID: " + readStatusId + "]");
  }
}
