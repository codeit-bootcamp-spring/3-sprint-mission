package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
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
public class JCFMessageService implements MessageService{
    private final ChannelService channelService;
    private final JCFMessageRepository jcfMessageRepository = new JCFMessageRepository();
//    private final Map<UUID,List<Message>> data;

    public JCFMessageService(ChannelService channelService) {
//        this.data = new HashMap<>();
        this.channelService = channelService;
    }

    @Override
    public UUID createMessage(UUID senderId, UUID channelId, String message) {
        List<Message> messages = new ArrayList<>();
        Message newMessage = new Message(senderId, channelId, message);
        messages.add(newMessage);
        jcfMessageRepository.createMessage(messages,channelId, newMessage);

//        if (data.get(channelId)==null) {
//            // 체널에 메세지가 없을 때
//            data.put(channelId, messages);
//        } else {
//            // 채널에 메세지가 있을 때
//            data.get(channelId).add(newMessage);
//        }
        channelService.addMessageInChannel(channelId, newMessage);

        return newMessage.getId();
    }

    @Override
    public List<Message> findAllMessages() {
        return jcfMessageRepository.findAllMessage();
//        return data.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public Message findMessageByMessageId(UUID messageId) {

//        List<Message> msgs = data.values().stream().flatMap(List::stream).collect(Collectors.toList());
//        for (Message msg : msgs) {
//            if (msg.getId().equals(messageId)) {
//                return msg;
//            }
//        }

        if (jcfMessageRepository.findMessageByMessageId(messageId)!=null) {
            return jcfMessageRepository.findMessageByMessageId(messageId);
        }
        return null;
    }

    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        jcfMessageRepository.updateMessage(newMessage, messageId);

//        List<Message> messages = data.get(channelId);
//
//        for (Message message : messages) {
//            if (message.getId().equals(messageId)) {
//                message.setMessage(newMessage);
//            }
//        }

    }

    @Override
    public void deleteMessageById(UUID messageId) {
        Message msg = findMessageByMessageId(messageId);

        if (msg == null) return;

        UUID channelId = msg.getChannelId();
        jcfMessageRepository.deleteMessageById(messageId);

//        List<Message> messages = data.get(channelId);

//        if (messages != null) {
//            deleteMessageById(messageId);
//            messages.removeIf(message -> message.getId().equals(messageId));
//        }

        Channel channel = channelService.findChannelById(channelId);
        if (channel != null) {
            channelService.deleteMessageInChannel(channelId, messageId);
        }
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        jcfMessageRepository.deleteMessagesByChannelId(channelId);

//        data.remove(channelId);
    }

}
