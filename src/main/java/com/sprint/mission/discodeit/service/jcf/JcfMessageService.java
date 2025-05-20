package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("jcf")
public class JcfMessageService {
    private final UserService userService;
    private final ChannelService channelService;
    private final JcfMessageRepository jcfMessageRepository;

    public final Map<UUID, Message> data = new HashMap<>();
//
//
//    // empty
//    @Override
//    public MessageCreateResponse createMessage(MessageCreateRequest request) {
//        return null;
//    }
//    // empty
//    @Override
//    public MessageAttachmentsCreateResponse createMessage(MessageAttachmentsCreateRequest request) {
//        return null;
//    }
//
//    // empty
//    @Override
//    public void updateMessage(MessageUpdateRequest request) {}
//
//    // empty
//    @Override
//    public List<Message> findAllByChannelId(UUID channelId) {
//        return List.of();
//    }
//
//    @Override
//    public Message findMessageById(UUID messageId) {
//        return jcfMessageRepository.findMessageById(messageId);
//    }
//
//    @Override
//    public List<Message> findAllMessages() {
//        return jcfMessageRepository.findAllMessages();
//    }
//
//    // this has to be updated
//    @Override
//    public void deleteMessage(UUID messageId) {
//        Objects.requireNonNull(messageId, "requre message id: JcfMessageService.deleteMessage");
//        jcfMessageRepository.deleteMessageById(messageId);
//    }

}
