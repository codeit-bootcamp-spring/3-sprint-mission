package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicMessageService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class BasicMessageService implements MessageService{
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository mr;

    public BasicMessageService(UserService userService, ChannelService channelService, MessageRepository mr) {
        this.userService = userService;
        this.channelService = channelService;
        this.mr = mr;
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        if (userService.findUserById(senderId) == null) {
            return null;
        }

        if (channelService.findChannelById(channelId) == null) {
            return null;
        }

        return mr.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return mr.findMessageById(messageId);
    }

    @Override
    public List<Message> findAllMessages() {
        return mr.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        mr.updateMessageById(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        mr.deleteMessageById(messageId);
    }

}
