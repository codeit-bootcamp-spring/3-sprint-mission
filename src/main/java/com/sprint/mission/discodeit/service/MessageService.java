package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageDto;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageDto create(MessageCreateRequest createRequest,
            List<BinaryContentCreateRequest> binaryContentCreateRequests);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    MessageDto find(UUID messageId);

    MessageDto update(UUID messageId, MessageUpdateRequest updateRequest);

    void delete(UUID messageId);
}
