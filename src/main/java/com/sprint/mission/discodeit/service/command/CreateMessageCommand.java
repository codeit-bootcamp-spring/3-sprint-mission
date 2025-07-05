package com.sprint.mission.discodeit.service.command;

import com.sprint.mission.discodeit.vo.BinaryContentData;
import java.util.List;
import java.util.UUID;

public record CreateMessageCommand(
    String content,
    UUID authorId,
    UUID channelId,
    List<BinaryContentData> attachments
) {

}
