package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class ParticipantAlreadyExistsException extends ChannelException {
    public ParticipantAlreadyExistsException(String userId, String channelId) {
        super(ErrorCode.PARTICIPANT_ALREADY_EXISTS,
                "이미 채널에 참여 중인 사용자입니다. [UserID: " + userId + ", ChannelID: " + channelId + "]");
    }
}
