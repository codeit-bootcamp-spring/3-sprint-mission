package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성
    Message createMessage(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests);

    // 채널의 메시지 조회
    List<Message> findallByChannelId(UUID channelId, UUID userId, String password);

    // 채널의 특정 메시지 조회
    Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId);

    // 유저가 보낸 메시지 조회
    List<Message> userMessage(UUID senderId, String password);

    // 메시지 수정
    boolean updateMessage(MessageUpdateRequest request);

    // 메시지 삭제
    void deletedMessage(UUID messageId, UUID senderId, String password);

}
