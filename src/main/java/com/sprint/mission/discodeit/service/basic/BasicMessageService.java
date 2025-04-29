package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

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
@Primary
@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService{

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return Optional.ofNullable(messageRepository.findMessagesByChannelId(channelId)).orElse(Collections.emptyList());
    }

    // message
    @Override
    public MessageCreateResponse createMessage(MessageCreateRequest request) {
        Channel channel = Optional.ofNullable(channelRepository.findChannelById(request.channelId())).orElseThrow(() -> new RuntimeException("채널 없음: BasicMessageService.createMessage"));
        User user = Optional.ofNullable(userRepository.findUserById(request.senderId())).orElseThrow(() -> new RuntimeException("유저 없음: BasicMessageService.createMessage"));
        String content = Optional.ofNullable(request.content()).orElseThrow(()->new RuntimeException("메세지 없음: BasicMessageService.createMessage"));

        Message message = messageRepository.createMessageWithContent(user.getId(), channel.getId(), content);

        return new MessageCreateResponse(
                message.getId(),
                message.getSenderId(),
                message.getChannelId(),
                message.getContent());
    }

    // binary content
    @Override
    public MessageAttachmentsCreateResponse createMessage(MessageAttachmentsCreateRequest request) {
        Channel channel = Optional.ofNullable(channelRepository.findChannelById(request.channelId())).orElseThrow(() -> new RuntimeException("채널 없음: BasicMessageService.createMessage"));
        User user = Optional.ofNullable(userRepository.findUserById(request.senderId())).orElseThrow(() -> new RuntimeException("유저 없음: BasicMessageService.createMessage"));
        List<byte[]> attachments = Optional.ofNullable(request.attachments()).orElseThrow(() -> new RuntimeException("첨부 파일 없음: BasicMessageService.createMessage"));

        List<UUID> attachmentIds = attachments.stream()
                .map(attachment -> binaryContentRepository.createBinaryContent(attachment).getId())
                .toList();

        Message message = messageRepository.createMessageWithAttachments(user.getId(), channel.getId(), attachmentIds);
        return new MessageAttachmentsCreateResponse(
                message.getId(),
                message.getSenderId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
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
    public void updateMessage(MessageUpdateRequest request) {
        messageRepository.updateMessageById(
                Optional.ofNullable(request.messageId()).orElseThrow(() -> new IllegalArgumentException("trouble with messageId : BasicMessageService.updateMessage")),
                Optional.ofNullable(request.content()).orElseThrow(()-> new IllegalArgumentException("trouble with content : BasicMessageService.updateMessage"))
        );
    }


    @Override
    public void deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new RuntimeException("require message Id : BasicMessageService.deleteMessage"));
        // attachments 삭제
        List<UUID> attachmentIds = messageRepository.findMessageById(messageId).getAttachmentIds();
        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.deleteBinaryContentById(attachmentId);
        }

        messageRepository.deleteMessageById(messageId);
    }



}
