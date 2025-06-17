package com.sprint.mission.discodeit.exception.custom.channel;

import com.sprint.mission.discodeit.exception.base.ChannelOperationNotAllowedException;

public class PrivateChannelModificationException extends ChannelOperationNotAllowedException {

    public PrivateChannelModificationException(String message) {
        super(message);
    }
}
