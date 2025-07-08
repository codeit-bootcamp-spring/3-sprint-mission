package com.sprint.mission.discodeit.exception.Channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.User.UserException;

public class PrivateChannelUpdateException extends UserException {

  public PrivateChannelUpdateException() {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
  }

  public PrivateChannelUpdateException(Channel channel) {
    super(ErrorCode.PRIVATE_CHANNEL_UPDATE,
        String.format("비공개채널은 업데이트 할수 없습니다: Id=%s", channel.getId()));
    super.addDetails("channel", channel);
  }
}
