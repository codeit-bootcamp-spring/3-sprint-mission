package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ResourceNotFoundException extends CommonException {

  public ResourceNotFoundException() {
    super(ErrorCode.RESOURCE_NOT_FOUND);
  }

  //Q. id로그만 찍는게 낫나?
  public ResourceNotFoundException(Object requestedInfo) {
    super(ErrorCode.RESOURCE_NOT_FOUND,
        String.format("요청한 리소스를 찾을 수 없습니다:providedInput=%s", requestedInfo.toString()));
    super.addDetails("requestedInfo", requestedInfo);
  }

  public ResourceNotFoundException(Object requestedInfo, Throwable cause) {
    super(ErrorCode.RESOURCE_NOT_FOUND,
        String.format("요청한 리소스를 찾을 수 없습니다:providedInput=%s", requestedInfo.toString()), cause);
    super.addDetails("requestedInfo", requestedInfo);

  }
}
