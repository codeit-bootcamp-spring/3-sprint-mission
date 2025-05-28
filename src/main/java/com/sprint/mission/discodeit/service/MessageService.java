package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.*;

public interface MessageService {
    Message create(MessageRequestDto messageRequestDTO, List<BinaryContentDto> binaryContentDtos);
    MessageResponseDto findById(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto updateContent(UUID messageId, String content);
    void deleteById(UUID messageId);
}
