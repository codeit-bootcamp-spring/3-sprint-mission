package com.sprint.mission.discodeit.service;


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
 */
public interface MessageService {

    Message createMessage(UUID senderId, UUID channelId, String content);

    Message findMessageById(UUID messageId);

    List<Message> findAllMessages();

    void updateMessage(UUID messageId, String content);

    void deleteMessage(UUID messageId);

}
