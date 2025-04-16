package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : MessageRepository
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface MessageRepository {
    Message saveMessage(UUID senderId, UUID channelId, String message);
    List<Message> findAllMessages();
    Message findMessageById(UUID messageId);
    void updateMessage(UUID messageId, String newMessage);
    void deleteMessageById(UUID messageId);
    void deleteMessagesByChannelId(UUID channelId);


}
