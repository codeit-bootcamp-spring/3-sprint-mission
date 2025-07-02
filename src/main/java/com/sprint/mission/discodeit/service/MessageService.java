package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageResponse create(
        MessageCreateRequest request,
        List<BinaryContentCreateRequest> attachments
    );

    MessageResponse find(UUID messageId);

    PageResponse<MessageResponse> findAllByChannelId(
        UUID channelId,
        Instant createdAt,
        Pageable pageable
    );

    MessageResponse update(UUID messageId, MessageUpdateRequest request);

    MessageResponse delete(UUID messageId);
}
