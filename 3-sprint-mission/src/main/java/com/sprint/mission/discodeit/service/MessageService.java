package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface MessageService {
    // id = message ID
    Message create(MessageCreateRequest messageCreateDTO,
                   List<BinaryContentCreateRequest> binaryContentCreateDTO);
    List<MessageDTO> findAllByChannelId(UUID channeId);
    MessageDTO find(UUID id);
    List<MessageDTO> findByContent(String content);
    Message update(UUID id, MessageUpdateRequest messageUpdateDTO);
    void delete(UUID id);

}
