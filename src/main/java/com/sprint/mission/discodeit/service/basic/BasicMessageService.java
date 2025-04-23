package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService{

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        Objects.requireNonNull(senderId,"no senderId: BasicMessageService.createMessage" );
        Objects.requireNonNull(channelId,"채널 id 없음: BasicMessageService.createMessage");
        Objects.requireNonNull(content,"메세지 내용 없음 BasicMessageService.createMessage");

        Objects.requireNonNull(userService.findUserById(senderId), "no user existing: createMessage");
        Objects.requireNonNull(channelService.findChannelById(channelId), "no channel existing: createMessage");

        return messageRepository.createMessageByUserIdAndChannelId(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Objects.requireNonNull(messageId, "no messageId: BasicMessageService.findMessageById");
        Message messageById = messageRepository.findMessageById(messageId);
        Objects.requireNonNull(messageById, "no message in DB: BasicMessageService.findMessageById");
        return messageById;
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "no messageId: BasicMessageService.updateMessage");
        Objects.requireNonNull(content, "no content: BasicMessageService.updateMessage");
        messageRepository.updateMessageById(messageId, content);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Objects.requireNonNull(messageId, "require message Id : BasicMessageService.deleteMessage");
        messageRepository.deleteMessageById(messageId);
    }

}
