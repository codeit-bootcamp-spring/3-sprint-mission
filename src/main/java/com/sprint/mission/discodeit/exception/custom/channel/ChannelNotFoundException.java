package com.sprint.mission.discodeit.exception.custom.channel;

import com.sprint.mission.discodeit.exception.base.NotFoundException;

public class ChannelNotFoundException extends NotFoundException {

    public ChannelNotFoundException(String message) {
        super(message);
    }
}
