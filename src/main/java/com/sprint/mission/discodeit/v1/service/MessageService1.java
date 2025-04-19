package com.sprint.mission.discodeit.v1.service;

import com.sprint.mission.discodeit.v1.entity.Message1;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : MessageService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public interface MessageService1 {
    UUID createMessage(UUID senderId, UUID channelId, String message);

    List<Message1> findAllMessages();

    Message1 findMessageByMessageId(UUID messageId);

    void updateMessage(UUID messageId, String newMessage);

    void deleteMessageById(UUID messageId);

    void deleteMessagesByChannelId(UUID channelId);





}
