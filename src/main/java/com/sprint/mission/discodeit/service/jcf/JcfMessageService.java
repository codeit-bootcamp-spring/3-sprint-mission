package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
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
    // 추가 void createMessage(String content);

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        if (userService.findUserById(senderId) == null) {
            return null;
        }

        if (channelService.findChannelById(channelId) == null) {
            return null;
        }
        return jmr.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    // 단건    Message findMessageById(UUID messageId);
    @Override
    public Message findMessageById(UUID messageId) {
        return jmr.findMessageById(messageId);
    }

    // 다건    List<Message> findAllMessages();
    @Override
    public List<Message> findAllMessages() {
        return jmr.findAllMessages();
    }

    // 업데이트   void updateMessage(UUID messageId, String content);
    @Override
    public void updateMessage(UUID messageId, String content) {
        jmr.updateMessageById(messageId, content);
    }

    // 삭제    void deleteMessage(UUID messageId);
    @Override
    public void deleteMessage(UUID messageId) {
        jmr.deleteMessageById(messageId);
    }

}
