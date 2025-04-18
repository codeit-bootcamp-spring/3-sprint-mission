package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

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
public class JcfMessageRepository implements MessageRepository {
    public final Map<UUID, Message> data = new HashMap<>();

//    Message createMessageUserIdAndChannelId(UUID senderId, UUID channelId, String content);
//    Message findMessageById(UUID messageId);
//    List<Message> findAllMessages();
//    void updateMessageById(UUID messageId, String content);
//    void deleteMessageById(UUID messageId);

    @Override
    public Message createMessageByUserIdAndChannelId(UUID senderId, UUID channelId, String content) {

        Message message = new Message(senderId, channelId, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findAllMessages() {
        data.values().stream().toList();
        return data.values().stream().toList();
    }

    @Override
    public void updateMessageById(UUID messageId, String content) {
        Message message2 = data.get(messageId);
        message2.setContent(content);
    }

    @Override
    public void deleteMessageById(UUID messageId) {
        data.remove(messageId);
    }
}
