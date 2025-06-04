package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests);
    Message get(UUID id);
    List<Message> getByChannel(UUID channelId);
    List<Message> getAll();
    Message update(UUID messageId, MessageUpdateRequest messageUpdateRequest);
    void delete(UUID id);
}