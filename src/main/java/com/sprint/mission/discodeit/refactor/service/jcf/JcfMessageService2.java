package com.sprint.mission.discodeit.refactor.service.jcf;

import com.sprint.mission.discodeit.refactor.entity.Message2;
import com.sprint.mission.discodeit.refactor.repository.jcf.JcfMessageRepository2;
import com.sprint.mission.discodeit.refactor.service.ChannelService2;
import com.sprint.mission.discodeit.refactor.service.MessageService2;
import com.sprint.mission.discodeit.refactor.service.UserService2;

import java.util.*;
import java.util.stream.Collectors;

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
public class JcfMessageService2 implements MessageService2 {
    private UserService2 userService;
    private ChannelService2 channelService;

    JcfMessageRepository2 jmr = new JcfMessageRepository2();

    public JcfMessageService2(UserService2 userService, ChannelService2 channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    public final Map<UUID, Message2> data = new HashMap<>();
    // 추가 void createMessage(String content);

    @Override
    public Message2 createMessage(UUID senderId, UUID channelId, String content) {
        if (userService.findUserById(senderId) == null) {
            return null;
        }

        if (channelService.findChannelById(channelId) == null) {
            return null;
        }
        return jmr.createMessage(senderId, channelId, content);
    }

    // 단건    Message findMessageById(UUID messageId);
    @Override
    public Message2 findMessageById(UUID messageId) {
        return jmr.findMessageById(messageId);
    }

    // 다건    List<Message> findAllMessages();
    @Override
    public List<Message2> findAllMessages() {
        return jmr.findAllMessages();
    }

    // 업데이트   void updateMessage(UUID messageId, String content);
    @Override
    public void updateMessage(UUID messageId, String content) {
        jmr.updateMessage(messageId, content);
    }

    // 삭제    void deleteMessage(UUID messageId);
    @Override
    public void deleteMessage(UUID messageId) {
        jmr.deleteMessage(messageId);
    }

}
