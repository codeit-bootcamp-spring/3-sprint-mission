package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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
@Service
@Profile("file")
@RequiredArgsConstructor
public class FileMessageService {
    private final FileMessageRepository fileMessageRepository;
    private final UserService userService;
    private final ChannelService channelService;

//    // empty
//    @Override
//    public void updateMessage(MessageUpdateRequest request) {
//
//    }
//
//    // empty
//    @Override
//    public List<Message> findAllByChannelId(UUID channelId) {
//        return List.of();
//    }
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
//
//
//    @Override
//    public Message findMessageById(UUID messageId) {
//        Objects.requireNonNull(messageId, "no messageId: FileMessageService.findMessageById");
//        Message messageById = fileMessageRepository.findMessageById(messageId);
//        Objects.requireNonNull(messageById, "no message in DB: FileMessageService.findMessageById");
//        return messageById;
//    }
//
//    @Override
//    public List<Message> findAllMessages() {
//        return fileMessageRepository.findAllMessages();
//    }
//
//
//    // this has to be updated
//    @Override
//    public void deleteMessage(UUID messageId) {
//        Objects.requireNonNull(messageId, "require message Id : FileMessageService.deleteMessage");
//        fileMessageRepository.deleteMessageById(messageId);
////    }
}
