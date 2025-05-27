package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;


public interface MessageService {
    Message create(MessageCreateRequest request);
    Message create(MessageCreateRequest request,List<BinaryContentCreateRequest> contentRequest);
    Message findById(UUID messageId);
    List<Message> findByChannel(UUID channelId);
    Message update(UUID messageId, MessageUpdateRequest request);
    void delete(UUID messageId);
}
