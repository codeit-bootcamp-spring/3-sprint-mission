package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  MessageDto create(MessageCreateRequest messageCreateRequest);

  MessageDto find(UUID messageId);

  List<MessageDto> findAllByChannelId(UUID channelId);

  PageResponse<MessageDto> findAllByChannelIdWithPaging(UUID channelId, Pageable pageable);

  PageResponse<MessageDto> findAllByChannelIdWithCursorPaging(UUID channelId, String cursor, Pageable pageable);

  MessageDto update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID messageId);
}
