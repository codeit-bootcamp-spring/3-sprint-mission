package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : JcfMessageService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;

    JcfMessageRepository jmr = new JcfMessageRepository();

    public JcfMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        Objects.requireNonNull(senderId,"no senderId: JcfMessageService.createMessage" );
        Objects.requireNonNull(channelId,"채널 id 없음: JcfMessageService.createMessage");
        Objects.requireNonNull(content,"메세지 내용 없음 JcfMessageService.createMessage");

        Objects.requireNonNull(userService.findUserById(senderId), "no user existing: JcfMessageService.createMessage");
        Objects.requireNonNull(channelService.findChannelById(channelId), "no channel existing: JcfMessageService.createMessage");
        return jmr.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return jmr.findMessageById(messageId);
    }

    @Override
    public List<Message> findAllMessages() {
        return jmr.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "no messageId: JcfMessageService.updateMessage");
        Objects.requireNonNull(content, "no content: JcfMessageService.updateMessage");
        jmr.updateMessageById(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Objects.requireNonNull(messageId, "requre message id: JcfMessageService.deleteMessage");
        jmr.deleteMessageById(messageId);
    }

}
