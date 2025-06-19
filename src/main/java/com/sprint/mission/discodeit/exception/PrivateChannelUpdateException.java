package com.sprint.mission.discodeit.exception;

import java.util.Map;

/**
 * PackageName  : com.sprint.mission.discodeit.exception
 * FileName     : PrivateChannelUpdateException
 * Author       : dounguk
 * Date         : 2025. 6. 18.
 */

public class PrivateChannelUpdateException extends ChannelException {
    public PrivateChannelUpdateException(Map<String, Object> details) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, details);
    }
    public PrivateChannelUpdateException() {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE);
    }
}
