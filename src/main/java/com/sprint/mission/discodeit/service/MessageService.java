package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    public MessageDto create(MessageCreateRequest messageCreateRequest,
                             List<BinaryContentCreateRequest> binaryContentCreateRequests);

    public MessageDto find(UUID messageId);

    public List<MessageDto> findAllByChannelId(UUID channelId);

    public MessageDto update(UUID messageId, MessageUpdateRequest request);

    public void delete(UUID messageId);
}
