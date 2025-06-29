package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageService {

    MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests);

    MessageDto find(UUID messageId);

    PageResponse<MessageDto> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt, Pageable pageable);

    MessageDto update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}
