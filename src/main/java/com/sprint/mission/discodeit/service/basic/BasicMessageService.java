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
    private static final String ATTACHMENT_PATH = "img/attachments";
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;


    @Override
    public ResponseEntity<List<Message>> findAllByChannelId(UUID channelId) {
        List<Message> messageList = Optional.ofNullable(messageRepository.findMessagesByChannelId(channelId)).orElse(Collections.emptyList());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageList);

    }

    // binary content
    @Override
    public ResponseEntity<?> createMessage(
            MessageAttachmentsCreateRequest request,
            List<BinaryContentCreateRequest> binaryContentRequests)
    {
        Optional.ofNullable(channelRepository.findChannelById(request.channelId())).orElseThrow(() -> new IllegalStateException("채널 없음: BasicMessageService.createMessage"));
        Optional.ofNullable(userRepository.findUserById(request.senderId())).orElseThrow(() -> new IllegalStateException("유저 없음: BasicMessageService.createMessage"));
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성

        List<UUID> attachmentIds = new ArrayList<>();

        for (BinaryContentCreateRequest singleRequest : binaryContentRequests) {
            // BinaryContent 생성
            BinaryContent attachment = binaryContentRepository.createBinaryContent(
                    singleRequest.fileName(),
                    (long) singleRequest.bytes().length,
                    singleRequest.contentType(),
                    singleRequest.bytes(),
                    singleRequest.fileName().substring(singleRequest.fileName().lastIndexOf("."))
            );
            // 이미지 저장
            String uploadFile = fileUploadUtils.getUploadPath(ATTACHMENT_PATH);

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

            attachmentIds.add(attachment.getId());
        }

        // 이미지 메세지 생성
        Message messageWithAttachments = messageRepository.createMessageWithAttachments(
                request.senderId(),
                request.channelId(),
                attachmentIds);

        // body
        MessageAttachmentsCreateResponse messageAttachmentsCreateResponse = new MessageAttachmentsCreateResponse(
                messageWithAttachments.getId(),
                messageWithAttachments.getSenderId(),
                messageWithAttachments.getChannelId(),
                messageWithAttachments.getAttachmentIds());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageAttachmentsCreateResponse);
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    }

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
    public ResponseEntity<?> updateMessage(MessageUpdateRequest request) {
        messageRepository.updateMessageById(request.messageId(),request.content());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "message updated"));
    }


    @Override
    public ResponseEntity<?> deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new IllegalArgumentException("require message Id : BasicMessageService.deleteMessage"));
        // attachments 삭제
        List<UUID> attachmentIds = Optional.ofNullable(messageRepository.findMessageById(messageId).getAttachmentIds()).orElse(null);
        if (attachmentIds != null) {
            for (UUID attachmentId : attachmentIds) {
                BinaryContent attachment = binaryContentRepository.findById(attachmentId);
                if (attachment != null) {
                    String directory = fileUploadUtils.getUploadPath(ATTACHMENT_PATH);
                    String extension = attachment.getExtension();
                    String fileName = attachmentId + extension;
                    File file = new File(directory, fileName);

                    if (file.exists()) {
                        boolean delete = file.delete();
                        if (!delete) {
                            throw new RuntimeException("could not delete file");
                        }
                    }
                }
                binaryContentRepository.deleteBinaryContentById(attachmentId); // throw exception if deletion fails
            }
        }
        messageRepository.deleteMessageById(messageId);  // throw exception

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("message", "message deleted"));
    }
}
