package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class JcfMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final JcfMessageRepository jcfMessageRepository;

    public final Map<UUID, Message> data = new HashMap<>();


    // empty
    @Override
    public Message createMessage(MessageCreateRequest request) {
        return null;
    }
    // empty
    @Override
    public Message createMessage(MessageAttachmentsCreateRequest request) {
        return null;
    }

    // empty
    @Override
    public void updateMessage(MessageUpdateRequest request) {

    }

    // empty
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public Message createMessage(UUID senderId, UUID channelId, String content) {
        Objects.requireNonNull(senderId,"no senderId: JcfMessageService.createMessage" );
        Objects.requireNonNull(channelId,"채널 id 없음: JcfMessageService.createMessage");
        Objects.requireNonNull(content,"메세지 내용 없음 JcfMessageService.createMessage");

        Objects.requireNonNull(userService.findUserById(senderId), "no user existing: JcfMessageService.createMessage");
        Objects.requireNonNull(channelService.findChannelById(channelId), "no channel existing: JcfMessageService.createMessage");
        return jcfMessageRepository.createMessageWithContent(senderId, channelId, content);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return jcfMessageRepository.findMessageById(messageId);
    }

    @Override
    public List<Message> findAllMessages() {
        return jcfMessageRepository.findAllMessages();
    }

    @Override
    public void updateMessage(UUID messageId, String content) {
        Objects.requireNonNull(messageId, "no messageId: JcfMessageService.updateMessage");
        Objects.requireNonNull(content, "no content: JcfMessageService.updateMessage");
        jcfMessageRepository.updateMessageById(messageId, content);
    }

    // this has to be updated
    @Override
    public void deleteMessage(UUID messageId) {
        Objects.requireNonNull(messageId, "requre message id: JcfMessageService.deleteMessage");
        jcfMessageRepository.deleteMessageById(messageId);
    }

}
