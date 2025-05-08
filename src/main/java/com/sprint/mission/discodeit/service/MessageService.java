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
    List<MessageResponseDTO> findAll();
    List<MessageResponseDTO> findAllByUserId(UUID userId);
    List<MessageResponseDTO> findMessageByContainingWord(String word);
    MessageResponseDTO updateBinaryContent(UUID messageId, List<BinaryContentDTO> binaryContentDTOS);
    MessageResponseDTO updateContent(UUID messageId, String content);
    void deleteById(UUID messageId);
}
