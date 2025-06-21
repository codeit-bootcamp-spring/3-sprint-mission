package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDTO;
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
  MessageDTO create(MessageCreateRequest messageCreateDTO,
      List<BinaryContentCreateRequest> binaryContentCreateDTO);

  PageResponse<MessageDTO> findAllByChannelId(UUID channeId, Instant cursor, Pageable pageable);

  MessageDTO find(UUID id);

  PageResponse<MessageDTO> findAllByChannelIdAndContent(UUID channelId, String content,
      Instant cursor, Pageable pageable);

  MessageDTO update(UUID id, MessageUpdateRequest messageUpdateDTO);

  void delete(UUID id);

}
