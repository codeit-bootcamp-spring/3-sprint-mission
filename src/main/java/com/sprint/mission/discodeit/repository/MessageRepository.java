package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Set;
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
// 사용 안함
public interface MessageRepository {
    Message createMessageWithContent(UUID senderId, UUID channelId, String content);

    Message findMessageById(UUID messageId); // file: null | jcf: null

    List<Message> findAllMessages(); // file: emptyList | jcf: emptyList

    void updateMessageById(UUID messageId, String content);

    void deleteMessageById(UUID messageId); // file, jcf: throw exception

    List<Message> findMessagesByChannelId(UUID channelId);

    Message createMessageWithAttachments(UUID userId, UUID channelId, List<UUID> attachments, String content);
}
