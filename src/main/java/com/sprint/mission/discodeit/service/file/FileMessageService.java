package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
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
        Objects.requireNonNull(senderId,"no senderId: FileMessageService.createMessage" );
        Objects.requireNonNull(channelId,"채널 id 없음: FileMessageService.createMessage");
        Objects.requireNonNull(content,"메세지 내용 없음 FileMessageService.createMessage");

        Objects.requireNonNull(userService.findUserById(senderId), "no user existing: createMessage");
        Objects.requireNonNull(channelService.findChannelById(channelId), "no channel existing: createMessage");

        return fmr.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Objects.requireNonNull(messageId, "no messageId: FileMessageService.findMessageById");
        Message messageById = fmr.findMessageById(messageId);
        Objects.requireNonNull(messageById, "no message in DB: FileMessageService.findMessageById");
        return messageById;
    }

    @Override
    public List<Message> findAllMessages() {
        return fmr.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "no messageId: FileMessageService.updateMessage");
        Objects.requireNonNull(content, "no content: FileMessageService.updateMessage");
        fmr.updateMessageById(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Objects.requireNonNull(messageId, "require message Id : FileMessageService.deleteMessage");
        fmr.deleteMessageById(messageId);
    }
}
