package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  Message create(MessageCreateRequest request);

  Message findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId);

  Message update(MessageUpdateRequest request);

  Message delete(UUID id);
}
