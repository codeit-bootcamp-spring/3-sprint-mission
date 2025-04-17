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


    public JCFMessageService(ChannelService channelService) {
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
    }

    @Override
    public Message findMessageByMessageId(UUID messageId) {


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
        Message msg = findMessageByMessageId(messageId);

        if (msg == null) return;

        UUID channelId = msg.getChannelId();
        jcfMessageRepository.deleteMessageById(messageId);


        Channel channel = channelService.findChannelById(channelId);
        if (channel != null) {
            channelService.deleteMessageInChannel(channelId, messageId);
        }
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        jcfMessageRepository.deleteMessagesByChannelId(channelId);

    }

}
