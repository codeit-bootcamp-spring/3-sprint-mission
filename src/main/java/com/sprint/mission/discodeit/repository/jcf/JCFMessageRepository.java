package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, List<Message>> messages;

    public JCFMessageRepository() {
        this.messages = new HashMap<>();
    }

    public Map<UUID, List<Message>> getMessages() {
        return messages;
    }

    @Override
    public Message createMessage(MessageCreateRequestDto messageCreateRequestDto) {
        List<Message> messages = this.messages.get(messageCreateRequestDto.getChannelId());
        if (messages == null) {
            messages = new ArrayList<>();
        }
        Message message = new Message(messageCreateRequestDto.getUserId(), messageCreateRequestDto.getMessageContent());
        messages.add(message);
        this.messages.put(messageCreateRequestDto.getChannelId(), messages);
        return message;
    }

    @Override
    public List<Message> getChannelMessages(UUID channelId) {
        return messages.get(channelId) == null ? List.of() : messages.get(channelId);
    }

    @Override
    public boolean updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
        List<Message> messages = this.messages.get(messageUpdateRequestDto.getChannelId());
        if (messages == null) {
            return false;
        }

        for (Message message : messages) {
            if (message.getId().equals(messageUpdateRequestDto.getMessageId())) {
                if (message.getSender().equals(messageUpdateRequestDto.getUserId())) {
                    message.setContent(messageUpdateRequestDto.getMessageContent());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto) {
        List<Message> messages = this.messages.get(messageDeleteRequestDto.getChannelId());

        for (Message message : messages) {
            if (message.getId().equals(messageDeleteRequestDto.getMessageId())) {
                if (message.getSender().equals(messageDeleteRequestDto.getUserId())) {
                    messages.remove(message);
                    System.out.println("메세지가 삭제 되었습니다.");
                    return;
                }
            }
        }
    }
}
