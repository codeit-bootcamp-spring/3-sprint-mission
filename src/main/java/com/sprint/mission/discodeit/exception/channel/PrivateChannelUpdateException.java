package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(UUID channelId) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, Map.of("수정하려고 한 채널 아이디: ", channelId));
    }
}
