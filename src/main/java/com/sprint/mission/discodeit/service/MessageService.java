package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto create(
      MessageCreateRequest request,
      List<MultipartFile> attachments
  );

  MessageDto find(UUID messageId);

  List<MessageDto> findAllByChannelId(UUID channelId, int page);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  MessageDto delete(UUID messageId);
}