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
import java.time.Instant;
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
public class BasicMessageService implements MessageService {
    private static final String ATTACHMENT_PATH = "img/attachments";
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final FileUploadUtils fileUploadUtils;


    @Override
    public ResponseEntity<?> findAllByChannelId(UUID channelId) {
        List<Message> messageList = messageRepository.findMessagesByChannelId(channelId);
        if (messageList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("any message in channel");
        }
        List<FoundMessagesResponse> responses = new ArrayList<>();
        for (Message message : messageList) {
            responses.add(new FoundMessagesResponse(
                    message.getId(),
                    message.getCreatedAt(),
                    message.getUpdatedAt(),
                    message.getContent(),
                    message.getChannelId(),
                    message.getSenderId(),
                    message.getAttachmentIds()
            ));
        }

        return ResponseEntity
                .status(200)
                .body(responses);
    }

    // binary content
    @Override
    public ResponseEntity<?> createMessage(
            MessageCreateRequest request,
            List<BinaryContentCreateRequest> binaryContentRequests) {

        if (channelRepository.findChannelById(request.channelId()) == null) {
            return ResponseEntity.status(404).body("channel with id " + request.channelId() + " not found");
        }
        if (userRepository.findUserById(request.authorId()) == null) {
            return ResponseEntity.status(404).body("author with id " + request.authorId() + " not found");
        }

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

        // 메세지 생성
        Message messageWithAttachments = messageRepository.createMessageWithAttachments(
                request.authorId(),
                request.channelId(),
                attachmentIds,
                request.content()
        );

        // body
        MessageAttachmentsCreateResponse messageAttachmentsCreateResponse = new MessageAttachmentsCreateResponse(
                messageWithAttachments.getId(),
                messageWithAttachments.getCreatedAt(),
                messageWithAttachments.getUpdatedAt(),
                messageWithAttachments.getContent(),
                messageWithAttachments.getSenderId(),
                messageWithAttachments.getChannelId(),
                messageWithAttachments.getAttachmentIds().stream().toList());

        return ResponseEntity
                .status(201)
                .body(messageAttachmentsCreateResponse);
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    }

    @Override
    public ResponseEntity<?> createMessage(MessageCreateRequest request) {
        Channel channel = channelRepository.findChannelById(request.channelId());
        User user = userRepository.findUserById(request.authorId());
        if (channel == null) {
            return ResponseEntity.status(404).body("channel with id " + request.channelId() + " not found");
        }
        if (user == null) {
            return ResponseEntity.status(404).body("author with id " + request.authorId() + " not found");
        }

        Message message = messageRepository.createMessageWithContent(user.getId(), channel.getId(), request.content());
        MessageAttachmentsCreateResponse response = new MessageAttachmentsCreateResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getSenderId(),
                message.getAttachmentIds().stream().toList()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
    public ResponseEntity<?> updateMessage(UUID messageId, MessageUpdateRequest request) {
        if (messageRepository.findMessageById(messageId) == null) {
            return ResponseEntity.status(404).body("message with id " + messageId + " not found");
        }

        messageRepository.updateMessageById(messageId, request.newContent());

        Message message = messageRepository.findMessageById(messageId);

        UpdateMessageResponse response = new UpdateMessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getSenderId(),
                message.getAttachmentIds()
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @Override
    public ResponseEntity<?> deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new IllegalArgumentException("require message Id : BasicMessageService.deleteMessage"));
        // attachments 삭제
        if (messageRepository.findMessageById(messageId) == null) {
            return ResponseEntity.status(404).body("Message with id " + messageId + " not found");
        }
        List<UUID> attachmentIds = messageRepository.findMessageById(messageId).getAttachmentIds();
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
        messageRepository.deleteMessageById(messageId);
        return ResponseEntity.status(204).body("");
    }
}
