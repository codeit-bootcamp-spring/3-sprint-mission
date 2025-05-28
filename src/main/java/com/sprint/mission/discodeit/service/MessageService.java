package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageRequestDto messageRequestDto, List<BinaryContentDto> binaryContentDtos);
    MessageResponseDto findById(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto updateContent(UUID messageId, String content);
    void deleteById(UUID messageId);
}
