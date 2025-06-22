package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ResourceNotFoundException extends CommonException {

  public ResourceNotFoundException() {
    super(ErrorCode.RESOURCE_NOT_FOUND);
  }

  //Q. id로그만 찍는게 낫나?
  public ResourceNotFoundException(String message) {
    super(ErrorCode.RESOURCE_NOT_FOUND,
        String.format("요청한 리소스를 찾을 수 없습니다: %s", message));
  }

  public ResourceNotFoundException(String message, Throwable cause) {
    super(ErrorCode.RESOURCE_NOT_FOUND,
        String.format("요청한 리소스를 찾을 수 없습니다: %s", message), cause);

  }
}
