package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final FileUploadUtils fileUploadUtils;


    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return Optional.ofNullable(messageRepository.findMessagesByChannelId(channelId)).orElse(Collections.emptyList());
    }

    // message
    @Override
    public ResponseEntity<MessageCreateResponse> createMessage(MessageCreateRequest request) {
        Channel channel = Optional.ofNullable(channelRepository.findChannelById(request.channelId())).orElseThrow(() -> new IllegalStateException("채널 없음: BasicMessageService.createMessage"));
        User user = Optional.ofNullable(userRepository.findUserById(request.senderId())).orElseThrow(() -> new IllegalStateException("유저 없음: BasicMessageService.createMessage"));
        String content = request.content();

        Message message = messageRepository.createMessageWithContent(user.getId(), channel.getId(), content);

        MessageCreateResponse messageCreateResponse = new MessageCreateResponse(
                message.getId(),
                message.getSenderId(),
                message.getChannelId(),
                message.getContent());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageCreateResponse);
    }

    // binary content
    // ❗️❗️❗️
    @Override
    public ResponseEntity<?> createMessage(
            MessageAttachmentsCreateRequest request,
            BinaryContentCreateRequest binaryContentRequest)
    {
        Optional.ofNullable(channelRepository.findChannelById(request.channelId())).orElseThrow(() -> new IllegalStateException("채널 없음: BasicMessageService.createMessage"));
        Optional.ofNullable(userRepository.findUserById(request.senderId())).orElseThrow(() -> new IllegalStateException("유저 없음: BasicMessageService.createMessage"));
//        List<byte[]> attachments = request.attachments();

//        createBinaryContent(String fileName, Long size, String contentType, byte[] bytes)

        List<UUID> attachmentIds = new ArrayList<>();

        // 단건 잘 되는지 테스트용
        BinaryContent attachment = binaryContentRepository.createBinaryContent(
                binaryContentRequest.fileName(),
                (long) binaryContentRequest.bytes().length,
                binaryContentRequest.contentType(),
                binaryContentRequest.bytes(),
                binaryContentRequest.fileName().substring(binaryContentRequest.fileName().lastIndexOf("."))
        );

        // file 저장 로직
        String uploadFile = fileUploadUtils.getUploadPath("img/attachments");

        String originalFileName = attachment.getFileName();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = attachment.getId() + extension;

        File attachmentFile = new File(uploadFile, newFileName);
        // 사진 저장
        try (FileOutputStream fos = new FileOutputStream(attachmentFile)) {
            fos.write(attachment.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("attachment not saved", e);
        }
//        binaryContentRequest를 리스트로 받게 바꿀 떈 아래를 사용
//        List<UUID> attachmentIds = attachments.stream()
//                .map(attachment -> binaryContentRepository.createBinaryContent().getId())
//                .toList();
//
//        // ⭐️️ 실제 사진 저장 로직 추가 필요
//
         // uuid 리스트로 저장
        attachmentIds.add(attachment.getId());

        // 이미지 메세지 생성
        Message messageWithAttachments = messageRepository.createMessageWithAttachments(
                request.senderId(),
                request.channelId(),
                attachmentIds);
//        Message message = messageRepository.createMessageWithAttachments(user.getId(), channel.getId(), attachmentIds);
//        return new MessageAttachmentsCreateResponse(
//                message.getId(),
//                message.getSenderId(),
//                message.getChannelId(),
//                message.getAttachmentIds()
//        );

//        UUID id, UUID senderId, UUID channelId, List<UUID> attachmentIds
        MessageAttachmentsCreateResponse messageAttachmentsCreateResponse = new MessageAttachmentsCreateResponse(
                messageWithAttachments.getId(),
                messageWithAttachments.getSenderId(),
                messageWithAttachments.getChannelId(),
                messageWithAttachments.getAttachmentIds());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageAttachmentsCreateResponse);
        // BinaryContent 생성 -> 이미지 저장 -> uuid로 변환 -> 메세지 생성
    }

    // not required
    @Override
    public Message findMessageById(UUID messageId) {
        Objects.requireNonNull(messageId, "no messageId: BasicMessageService.findMessageById");
        return Optional.ofNullable(messageRepository.findMessageById(messageId))
                .orElseThrow(() -> new IllegalStateException("no message in DB: BasicMessageService.findMessageById"));
    }
    // not required
    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAllMessages();
    }


    @Override
    public void updateMessage(MessageUpdateRequest request) {
        messageRepository.updateMessageById(request.messageId(),request.content());
    }


    @Override
    public void deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new IllegalArgumentException("require message Id : BasicMessageService.deleteMessage"));
        // attachments 삭제
        List<UUID> attachmentIds = Optional.ofNullable(messageRepository.findMessageById(messageId).getAttachmentIds()).orElse(null);
        if (attachmentIds != null) {
            for (UUID attachmentId : attachmentIds) {
                binaryContentRepository.deleteBinaryContentById(attachmentId); // throw exception if deletion fails
            }
        }
        messageRepository.deleteMessageById(messageId);  // throw exception
    }
}
