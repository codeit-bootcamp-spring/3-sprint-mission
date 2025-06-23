package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ParticipantNotFoundException extends ChannelException {
    public ParticipantNotFoundException(String userId, String channelId) {
        super(ErrorCode.PARTICIPANT_NOT_FOUND,
                "채널에서 사용자를 찾을 수 없습니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
}
