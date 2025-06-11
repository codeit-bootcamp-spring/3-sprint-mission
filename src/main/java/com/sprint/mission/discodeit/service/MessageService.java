package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    public MessageDto create(MessageCreateRequest request,
                             List<BinaryContentCreateRequest> attachmentRequests);

    public MessageDto find(UUID id);

    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
                                                       Pageable pageable);

    public MessageDto update(UUID messageId, MessageUpdateRequest request);

    public void delete(UUID id);

}