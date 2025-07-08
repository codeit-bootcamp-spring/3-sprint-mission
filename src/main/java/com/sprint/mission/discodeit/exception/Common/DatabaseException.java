package com.sprint.mission.discodeit.exception.Common;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class DatabaseException extends CommonException {

  public DatabaseException() {
    super(ErrorCode.DATABASE_ERROR);
  }

  public DatabaseException(String message) {
    super(ErrorCode.DATABASE_ERROR, message);
  }

  public DatabaseException(String message, Throwable cause) {
    super(ErrorCode.DATABASE_ERROR, message, cause);
  }
}
