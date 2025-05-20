package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entitiy.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  public Message create(MessageCreateRequest request,
      Optional<List<BinaryContentCreateRequest>> binaryContentRequest);

  public List<Message> findAllByChannelId(UUID channelId);

  public void update(UUID messageId, MessageUpdateRequest request,
      Optional<List<BinaryContentCreateRequest>> binaryContentRequest);

  public void delete(UUID messageId);

}
