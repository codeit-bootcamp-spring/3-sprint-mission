package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFMessageService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    : implements MessageService
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 * 2025. 4. 5.        doungukkim       List<Message> 구조에서 Map<UUID, List<Message>> 구조로 수정
 */
public class JCFMessageService implements MessageService {
    private final ChannelService channelService;
    private final Map<UUID,List<Message>> data;

    public JCFMessageService(ChannelService channelService) {
        this.data = new HashMap<>();
        this.channelService = channelService;
    }

    @Override
    public UUID createMessage(UUID senderId, UUID channelId, String message) {
        List<Message> messageList = new ArrayList<>();
        Message newMessage = new Message(senderId, channelId, message);
        messageList.add(newMessage);

        if (data.get(channelId)==null) {
            // 체널에 메세지가 없을 때
            data.put(channelId, messageList);
        } else {
            // 채널에 메세지가 있을 때
            data.get(channelId).add(newMessage);
        }
        channelService.addMessageInChannel(channelId, newMessage);

        return newMessage.getId();
    }

    @Override
    public List<Message> findAllMessages() {
        return data.values().stream().flatMap(List::stream).collect(Collectors.toList());

    }

    @Override
    public Message findMessageByMessageId(UUID messageId) {

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
        UUID channelId = findMessageByMessageId(messageId).getChannelId();
        List<Message> messages = data.get(channelId);

        for (Message message : messages) {
            if (message.getId().equals(messageId)) {
                message.setMessage(newMessage);
            }
        }
    }

    @Override
    public void deleteMessageById(UUID messageId) {

        UUID channelId = findMessageByMessageId(messageId).getChannelId();
        List<Message> messages = data.get(channelId);

        // 비어있는게 아니면 메세지 삭제
        if(!messages.isEmpty()){
            messages.removeIf(message -> message.getId().equals(messageId));
        }
        // 채널에 있는 메세지 삭제
        channelService.deleteMessageInChannel(messageId);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        if (data.containsKey(channelId)) {
            data.remove(channelId);
        }
    }

}
