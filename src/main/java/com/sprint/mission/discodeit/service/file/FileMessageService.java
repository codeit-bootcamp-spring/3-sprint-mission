package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

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
public class FileMessageService implements MessageService {
    FileMessageRepository fmr = new FileMessageRepository();

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        if (userService.findUserById(senderId) == null) {
            return null;
        }

        if (channelService.findChannelById(channelId) == null) {
            return null;
        }

        return fmr.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return fmr.findMessageById(messageId);
    }

    @Override
    public List<Message> findAllMessages() {
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
