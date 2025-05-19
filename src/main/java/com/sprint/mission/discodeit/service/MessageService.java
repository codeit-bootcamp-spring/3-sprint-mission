package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 생성
    Message create(MessageCreateRequest createRequestDTO,
            List<BinaryContentCreateRequest> binaryContentCreateRequestsDTO);

    // 조회 (채널)
    List<Message> findAllByChannelId(UUID channelId);

    // 조회 (ID)
    Message find(UUID messageId);

    // 수정
    Message update(UUID messageId, MessageUpdateRequest updateRequestDTO);

    // 삭제
    void delete(UUID messageId);
}
