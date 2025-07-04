package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

  // id = message ID
  MessageDto create(MessageCreateRequest messageCreateDto,
      List<BinaryContentCreateRequest> binaryContentCreateDto);

  PageResponse<MessageDto> findAllByChannelId(UUID channeId, Instant cursor, Pageable pageable);

  MessageDto find(UUID id);

  PageResponse<MessageDto> findAllByChannelIdAndContent(UUID channelId, String content,
      Instant cursor, Pageable pageable);

  MessageDto update(UUID id, MessageUpdateRequest messageUpdateDto);

  void delete(UUID id);

}
