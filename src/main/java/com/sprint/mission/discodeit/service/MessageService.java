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
    //  input == null
    //    찾는 유저가 없다면?
    //    찾는 채널이 없다면?
    Message createMessage(UUID senderId, UUID channelId, String content);
    //  input == null
    //    찾는 메세지가 없다면?
    Message findMessageById(UUID messageId);
    //  input == null
    //    메세지가 없다면?
    List<Message> findAllMessages();
    //  input == null
    //    찾는 메세지가 없다면?
    void updateMessage(UUID messageId, String content);
// input == null
    //    찾는 메세지가 없다면?
    void deleteMessage(UUID messageId);

}
