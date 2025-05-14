package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    public Message create(CreateMessageRequest request, Optional<List<CreateBinaryContentRequest>> binaryContentRequest);
    public List<Message> findAllByChannelId(UUID channelId);
    public void update(UpdateMessageRequest request,Optional<List<CreateBinaryContentRequest>> binaryContentRequest);
    public void delete(UUID messageId);

}
