package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JCFMessageRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> data = new HashMap<>();

    @Override
    public Message saveMessage(UUID senderId, UUID channelId, String message) {
        List<Message> messages = new ArrayList<>();
        Message msg = new Message(senderId, channelId, message);
        messages.add(msg);

        if (data.get(channelId)==null) {
            // 체널에 메세지가 없을 때
            data.put(channelId, messages);
        } else {
            // 채널에 메세지가 있을 때
            data.get(channelId).add(msg);
        }
        return msg;
    }

    @Override
    public List<Message> findAllMessages() {
        return data.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    @Override
    public Message findMessageById(UUID messageId) {
        List<Message> msgs = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
        for (Message msg : msgs) {
            if (msg.getId().equals(messageId)) {
                return msg;
            }
        }
        return null;
    }
    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        UUID channelId = findMessageById(messageId).getChannelId();
        List<Message> messages = data.get(channelId);

        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.setMessage(newMessage);
            }
        }
    }
    @Override
    public void deleteMessageById(UUID messageId) {
        Message msg = findMessageById(messageId);
        UUID channelId = msg.getChannelId();
        List<Message> messages = data.get(channelId);
        if (messages != null) {
            messages.removeIf(message -> message.getId().equals(messageId));
        }
    }
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        data.remove(channelId);
    }
}
