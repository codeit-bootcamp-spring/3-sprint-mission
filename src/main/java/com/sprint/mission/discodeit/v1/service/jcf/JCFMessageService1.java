package com.sprint.mission.discodeit.v1.service.jcf;

import com.sprint.mission.discodeit.v1.entity.Channel1;
import com.sprint.mission.discodeit.v1.entity.Message1;
import com.sprint.mission.discodeit.v1.repository.jcf.JCFMessageRepository1;
import com.sprint.mission.discodeit.v1.service.ChannelService1;
import com.sprint.mission.discodeit.v1.service.MessageService1;

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
public class JCFMessageService1 implements MessageService1 {
    private final ChannelService1 channelService;
    private final JCFMessageRepository1 jcfMessageRepository = new JCFMessageRepository1();


    public JCFMessageService1(ChannelService1 channelService) {
        this.channelService = channelService;
    }



    @Override
    public UUID createMessage(UUID senderId, UUID channelId, String message) {
        Message1 newMessage = jcfMessageRepository.saveMessage(senderId, channelId, message);
        channelService.addMessageInChannel(channelId, newMessage);

        return newMessage.getId();
    }

    @Override
    public List<Message1> findAllMessages() {
        return jcfMessageRepository.findAllMessages();
    }

    @Override
    public Message1 findMessageByMessageId(UUID messageId) {


        if (jcfMessageRepository.findMessageById(messageId)!=null) {
            return jcfMessageRepository.findMessageById(messageId);
        }
        return null;
    }

    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        jcfMessageRepository.updateMessage(messageId, newMessage);

    }

    @Override
    public void deleteMessageById(UUID messageId) {
        Message1 msg = findMessageByMessageId(messageId);

        if (msg == null) return;

        UUID channelId = msg.getChannelId();
        jcfMessageRepository.deleteMessageById(messageId);


        Channel1 channel = channelService.findChannelById(channelId);
        if (channel != null) {
            channelService.deleteMessageInChannel(channelId, messageId);
        }
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        jcfMessageRepository.deleteMessagesByChannelId(channelId);

    }

}
