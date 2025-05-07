package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateMessageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(CreateMessageRequest request);
    Message find(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UpdateMessageRequest request);
    void delete(UUID messageId);
}
