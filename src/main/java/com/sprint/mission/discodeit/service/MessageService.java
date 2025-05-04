package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.dto.Message.MessageUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    public Message create(MessageCreateRequest messageCreateRequest,
                          List<BinaryContentCreateRequest> binaryContentCreateRequests);

    public Message find(UUID messageId);

    public List<Message> findAllByChannelId(UUID channelId);

    public Message update(MessageUpdateRequest request);

    public void delete(UUID messageId);
}
