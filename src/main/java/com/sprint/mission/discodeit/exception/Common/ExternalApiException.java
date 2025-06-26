package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ExternalApiException extends CommonException {

  public ExternalApiException() {
    super(ErrorCode.EXTERNAL_API_ERROR);
  }

  public ExternalApiException(Object apiInfo) {
    super(ErrorCode.EXTERNAL_API_ERROR);
    super.addDetails("apiInfo", apiInfo);
  }

  public ExternalApiException(Object apiInfo, Throwable cause) {
    super(ErrorCode.EXTERNAL_API_ERROR, ErrorCode.EXTERNAL_API_ERROR.getDefaultMessage(), cause);
    super.addDetails("apiInfo", apiInfo);
  }
}
