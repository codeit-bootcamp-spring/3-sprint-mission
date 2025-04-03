package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

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
public interface MessageService {
    UUID createMessage(UUID senderId, UUID channelId, String message);

    List<String> findAllMessages();

    List<String> findMessageById(UUID id);

    void updateMessage(UUID id, String newMessage);

    void deleteMessageById(UUID id);


}
