package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
 * fileName       : MessageService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 * 2025. 4. 28.        doungukkim       createMessage(DTO) 두개 완성, 테스트 필요
 */
public interface MessageService {

    // 삭제 예정
    Message createMessage(UUID senderId, UUID channelId, String content);


    Message createMessage(MessageCreateRequest request);
    Message createMessage(MessageAttachmentsCreateRequest request);

    Message findMessageById(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);

    List<Message> findAllMessages();

    // legacy
    void updateMessage(UUID messageId, String content);

    void updateMessage(MessageUpdateRequest request);


    void deleteMessage(UUID messageId);



}
