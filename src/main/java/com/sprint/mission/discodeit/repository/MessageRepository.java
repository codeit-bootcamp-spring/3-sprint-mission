package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository
 * fileName       : MessageRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public interface MessageRepository {
    Message createMessageByUserIdAndChannelId(UUID senderId, UUID channelId, String content);
    Message findMessageById(UUID messageId);
    List<Message> findAllMessages();
    void updateMessageById(UUID messageId, String content);
    void deleteMessageById(UUID messageId);
    List<Message> findMessagesByChanenlId(UUID channelId);
}
