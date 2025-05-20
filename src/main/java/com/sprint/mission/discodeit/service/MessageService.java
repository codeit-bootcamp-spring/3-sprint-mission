package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageResponse create(
      MessageCreateRequest request,
      List<MultipartFile> attachments
  );

  MessageResponse find(UUID messageId);

  List<Message> findAllByChannelId(UUID channelId);

  MessageResponse update(UUID messageId, MessageUpdateRequest request);

  MessageResponse delete(UUID messageId);
}