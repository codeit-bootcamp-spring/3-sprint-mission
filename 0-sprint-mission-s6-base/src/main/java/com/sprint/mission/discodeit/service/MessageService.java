package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageResponse create(MessageRequest.Create request,
      List<MultipartFile> messagesFiles);

  MessageResponse find(UUID id);

  PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Pageable pageable);

  MessageResponse update(UUID id, MessageRequest.Update request);

  void delete(UUID id);
}
