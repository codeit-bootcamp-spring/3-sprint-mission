package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    // 메시지 생성
    MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests);

    // 채널 메시지 조회
    PageResponse<MessageDto> getAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    // 채널의 특정 메시지 조회
    MessageDto find(UUID messageId);

    // 메시지 수정
    MessageDto update(UUID messageId, MessageUpdateRequest request);

    // 메시지 삭제
    void delete(UUID messageId);

}
