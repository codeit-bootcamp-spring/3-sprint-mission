package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository extends AbstractFileRepository<UUID, List<Message>> implements MessageRepository {

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/message.ser");
    }

    @Override
    public Message createMessage(MessageCreateRequestDto messageCreateRequestDto) {
        Map<UUID, List<Message>> messages = loadFromFile();

        List<Message> channelMessages = messages.get(messageCreateRequestDto.getChannelId());
        if (channelMessages == null) {
            channelMessages = new ArrayList<>();
        }

        Message message = new Message(messageCreateRequestDto.getUserId(), messageCreateRequestDto.getMessageContent());
        channelMessages.add(message);
        messages.put(messageCreateRequestDto.getChannelId(), channelMessages);
        saveToFile(messages);
        return message;
    }

    @Override
    public List<Message> getChannelMessages(UUID channelId) {
        Map<UUID, List<Message>> messages = loadFromFile();
        return messages.get(channelId) == null ? List.of() : messages.get(channelId);
    }

    @Override
    public boolean updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
        Map<UUID, List<Message>> messages = loadFromFile();
        if (messages.containsKey(messageUpdateRequestDto.getChannelId())) {
            List<Message> channelMessages = messages.get(messageUpdateRequestDto.getChannelId());
            for (Message message : channelMessages) {
                System.out.println(message.getId());
                if (message.getId().equals(messageUpdateRequestDto.getMessageId())) {
                    if (message.getSender().equals(messageUpdateRequestDto.getUserId())) {
                        message.setContent(messageUpdateRequestDto.getMessageContent());
                        saveToFile(messages);
                        return true;
                    }
                }
            }
        } else {
            System.out.println("존재하지 않는 채널입니다.");
        }
        return false;
    }

    @Override
    public void deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto) {
        Map<UUID, List<Message>> messages = loadFromFile();
        List<Message> messageList = messages.get(messageDeleteRequestDto.getChannelId());
        for (Message message : messageList) {
            if (message.getId().equals(messageDeleteRequestDto.getMessageId())) {
                if (message.getSender().equals(messageDeleteRequestDto.getUserId())) {
                    messageList.remove(message);
                    System.out.println("메세지가 삭제 되었습니다.");
                    saveToFile(messages);
                    return;
                }
            }
        }
    }
}
