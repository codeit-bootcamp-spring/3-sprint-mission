package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class CannotUpdatePrivateChannelException extends ChannelException {
    public CannotUpdatePrivateChannelException(String channelId) {
        super(ErrorCode.CANNOT_UPDATE_PRIVATE_CHANNEL, "비공개 채널은 수정할 수 없습니다. [ChannelID: " + channelId + "]");
    }
}
