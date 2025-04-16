package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

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
        Message newMessage = jcfMessageRepository.saveMessage(senderId, channelId, message);
        channelService.addMessageInChannel(channelId, newMessage);

        return newMessage.getId();
    }

    @Override
    public List<Message> findAllMessages() {
        return jcfMessageRepository.findAllMessages();
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

        if (jcfMessageRepository.findMessageById(messageId)!=null) {
            return jcfMessageRepository.findMessageById(messageId);
        }
        return null;
    }

    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        jcfMessageRepository.updateMessage(messageId, newMessage);

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
