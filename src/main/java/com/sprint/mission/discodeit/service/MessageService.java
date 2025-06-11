package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateRequest req, List<BinaryContentCreateRequest> attachments) throws IOException;

    MessageDto find(UUID messageId);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, String cursor, int size);

    MessageDto update(UUID messageId, MessageUpdateRequest req);

    void delete(UUID messageId);
}