package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import java.util.Map;
import java.util.UUID;

public class FileNotFoundException extends ChannelException {

    public FileNotFoundException(UUID binaryContentId) {
        super(ErrorCode.FILE_NOT_FOUND, Map.of("binaryContentId", binaryContentId));
    }

}
