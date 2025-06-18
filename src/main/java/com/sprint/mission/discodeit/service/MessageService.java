package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageRequestDto messageRequestDto, List<BinaryContentDto> binaryContentDtos);

    MessageResponseDto findById(UUID messageId);

    PageResponse<MessageResponseDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    MessageResponseDto updateContent(UUID messageId, String content);

    void deleteById(UUID messageId);
}
