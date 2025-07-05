package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class UserNotParticipantException extends UserException {
  public UserNotParticipantException() {
    super(ErrorCode.UNAUTHORIZED, "채널 참여자가 아닙니다.");
  }
}
