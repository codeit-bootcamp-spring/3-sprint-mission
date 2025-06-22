package com.sprint.mission.discodeit.exception.Message;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.User.UserException;

public class AccessDeniedMessageException extends UserException {

  public AccessDeniedMessageException() {
    super(ErrorCode.ACCESS_DENIED_MESSAGE);
  }

  public AccessDeniedMessageException(User user, Channel channel) {
    super(ErrorCode.ACCESS_DENIED_MESSAGE,
        String.format("유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다: UserId=%s , ChannelId=%s", user.getId(),
            channel.getId()));
    super.addDetails("user", user);
    super.addDetails("channel", channel);
  }
}
