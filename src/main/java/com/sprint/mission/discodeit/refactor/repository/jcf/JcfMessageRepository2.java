package com.sprint.mission.discodeit.refactor.repository.jcf;

import com.sprint.mission.discodeit.refactor.entity.Message2;
import com.sprint.mission.discodeit.refactor.service.MessageService2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.jcf
 * fileName       : JcfMessageRepository
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfMessageRepository2 implements MessageService2 {
    public final Map<UUID, Message2> data = new HashMap<>();

    @Override
    public Message2 createMessage(UUID senderId, UUID channelId, String content) {

        Message2 message = new Message2(senderId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message2 findMessageById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message2> findAllMessages() {
        data.values().stream().toList();
        return data.values().stream().toList();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Message2 message2 = data.get(messageId);
        message2.setContent(content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        data.remove(messageId);
    }
}
