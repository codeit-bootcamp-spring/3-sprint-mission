package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest request, List<BinaryContentCreateRequest> attachments);
    List<Message> findAllByChannelId(UUID channelId);
    Message update(MessageUpdateRequest request);
    void deleteById(UUID messageId);

//    Message createMessage(Message message);
//    Optional<Message> getMessage(UUID messageId);
//    List<Message> getAllMessages();
//    void updateMessage(UUID messageId, String msgContent);
//    void deleteMessage(UUID messageId);
}
