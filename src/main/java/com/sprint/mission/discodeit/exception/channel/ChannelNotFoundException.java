package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException(String channelId) {
        super(ErrorCode.CHANNEL_NOT_FOUND, "해당 채널을 찾을 수 없습니다. [ChannelID: " + channelId + "]");
    }
}
