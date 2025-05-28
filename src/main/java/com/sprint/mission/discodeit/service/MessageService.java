package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.Message;
import java.util.*;

public interface MessageService {
    Message create(MessageRequestDTO messageRequestDTO, List<BinaryContentDTO> binaryContentDTOS);
    MessageResponseDTO findById(UUID messageId);
    List<MessageResponseDTO> findAllByChannelId(UUID channelId);
    MessageResponseDTO updateContent(UUID messageId, String content);
    void deleteById(UUID messageId);
}
