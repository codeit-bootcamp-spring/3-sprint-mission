package com.sprint.mission.discodeit.v1.repository.jcf;

import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.MessageRepository1;

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
public class JCFMessageRepository1 implements MessageRepository1 {
    private final Map<UUID, List<Message1>> data = new HashMap<>();

    @Override
    public Message1 saveMessage(UUID senderId, UUID channelId, String message) {
        List<Message1> messages = new ArrayList<>();
        Message1 msg = new Message1(senderId, channelId, message);
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
    public List<Message1> findAllMessages() {
        return data.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    @Override
    public Message1 findMessageById(UUID messageId) {
        List<Message1> msgs = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
        for (Message1 msg : msgs) {
            if (msg.getId().equals(messageId)) {
                return msg;
            }
        }
        return null;
    }
    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        UUID channelId = findMessageById(messageId).getChannelId();
        List<Message1> messages = data.get(channelId);

        for (Message1 message : messages) {
            if (message.getId().equals(messageId)) {
                message.setMessage(newMessage);
            }
        }
    }
    @Override
    public void deleteMessageById(UUID messageId) {
        Message1 msg = findMessageById(messageId);
        UUID channelId = msg.getChannelId();
        List<Message1> messages = data.get(channelId);
        if (messages != null) {
            messages.removeIf(message -> message.getId().equals(messageId));
        }
    }
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        data.remove(channelId);
    }
}
