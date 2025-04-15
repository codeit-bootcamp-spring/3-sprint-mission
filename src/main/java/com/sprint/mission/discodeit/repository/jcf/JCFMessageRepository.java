package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
public class JCFMessageRepository {
    private final Map<UUID, List<Message>> data = new HashMap<>();

    public void createMessage(List<Message> messages, UUID channelId, Message newMessage) {
        if (data.get(channelId)==null) {
            // 체널에 메세지가 없을 때
            data.put(channelId, messages);
        } else {
            // 채널에 메세지가 있을 때
            data.get(channelId).add(newMessage);
        }
    }

    public List<Message> findAllMessage() {
        return data.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public Message findMessageByMessageId(UUID messageId) {
        List<Message> msgs = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
        for (Message msg : msgs) {
            if (msg.getId().equals(messageId)) {
                return msg;
            }
        }
        return null;
    }

    public void updateMessage(String newMessage, UUID messageId) {
        UUID channelId = findMessageByMessageId(messageId).getChannelId();
        List<Message> messages = data.get(channelId);

        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.setMessage(newMessage);
            }
        }
    }

    public void deleteMessageById(UUID messageId) {
        Message msg = findMessageByMessageId(messageId);
        UUID channelId = msg.getChannelId();
        List<Message> messages = data.get(channelId);
        if (messages != null) {
            messages.removeIf(message -> message.getId().equals(messageId));
        }
    }

    public void deleteMessagesByChannelId(UUID channelId) {
        data.remove(channelId);
    }
}
