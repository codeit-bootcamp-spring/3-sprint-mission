package com.sprint.mission.discodeit.refactor.service.file;

import com.sprint.mission.discodeit.refactor.entity.Message2;
import com.sprint.mission.discodeit.refactor.repository.file.FileMessageRepository2;
import com.sprint.mission.discodeit.refactor.service.ChannelService2;
import com.sprint.mission.discodeit.refactor.service.MessageService2;
import com.sprint.mission.discodeit.refactor.service.UserService2;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service.file
 * fileName       : FileMessageService2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class FileMessageService2 implements MessageService2 {
    FileMessageRepository2 fmr = new FileMessageRepository2();

    private final UserService2 userService;
    private final ChannelService2 channelService;

    public FileMessageService2(UserService2 userService, ChannelService2 channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message2 createMessage(UUID senderId, UUID channelId, String content) {
        if (userService.findUserById(senderId) == null) {
            return null;
        }

        if (channelService.findChannelById(channelId) == null) {
            return null;
        }

        return fmr.createMessageUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message2 findMessageById(UUID messageId) {
        return fmr.findMessageById(messageId);
    }

    @Override
    public List<Message2> findAllMessages() {
        return fmr.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        fmr.updateMessageById(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        fmr.deleteMessageById(messageId);
    }
}
