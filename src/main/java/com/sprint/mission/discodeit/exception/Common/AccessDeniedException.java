package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class AccessDeniedException extends CommonException {

  public AccessDeniedException() {
    super(ErrorCode.ACCESS_DENIED);
  }

  public AccessDeniedException(String message) {
    super(ErrorCode.ACCESS_DENIED,
        String.format("접근 권한이 없습니다: %s", message));
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(ErrorCode.ACCESS_DENIED,
        String.format("접근 권한이 없습니다: %s", message), cause);
  }
}
