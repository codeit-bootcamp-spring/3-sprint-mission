package com.sprint.mission.discodeit.refactor.service;


import com.sprint.mission.discodeit.refactor.entity.Message2;

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
 */
public interface MessageService2 {
    Message2 createMessage(UUID senderId, UUID channelId, String content);
    Message2 findMessageById(UUID messageId);
    List<Message2> findAllMessages();
    void updateMessage(UUID messageId, String content);
    void deleteMessage(UUID messageId);

}
