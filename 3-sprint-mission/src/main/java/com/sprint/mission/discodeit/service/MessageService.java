package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface MessageService {
    // id = message ID
    Message create(MessageCreateRequest messageCreateDTO,
                   List<BinaryContentCreateRequest> binaryContentCreateDTO);
    List<Message> findAllByChannelId(UUID channeId);
    Message find(UUID id);
    List<Message> findByText(String text);
    Message update(UUID id, MessageUpdateRequest messageUpdateDTO);
    void delete(UUID id);

}
