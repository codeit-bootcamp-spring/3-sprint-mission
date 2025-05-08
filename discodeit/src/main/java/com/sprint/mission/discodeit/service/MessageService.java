package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(CreateMessageRequest request);
    Message find(UUID messageId);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(UpdateMessageRequest updateMessageRequest);
    void delete(UUID messageId);
}
