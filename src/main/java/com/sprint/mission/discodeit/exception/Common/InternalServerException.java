package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InternalServerException extends CommonException {

  public InternalServerException() {
    super(ErrorCode.INTERNAL_SERVER_ERROR);
  }

  public InternalServerException(Object apiInfo) {
    super(ErrorCode.INTERNAL_SERVER_ERROR);
    super.addDetails("apiInfo", apiInfo);
  }

  public InternalServerException(Object apiInfo, Throwable cause) {
    super(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
        cause);
    super.addDetails("apiInfo", apiInfo);
  }
}
