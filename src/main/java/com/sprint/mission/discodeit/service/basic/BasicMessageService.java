package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
@Transactional
@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private static final String ATTACHMENT_PATH = "img/attachments";
    private final FileUploadUtils fileUploadUtils;
    private final JpaMessageRepository messageRepository;
    private final JpaChannelRepository channelRepository;
    private final JpaUserRepository userRepository;
    private final JpaBinaryContentRepository binaryContentRepository;
    private final JpaReadStatusRepository readStatusRepository;


    @Override
    public List<FoundMessagesResponse> findAllByChannelId(UUID channelId) {
        List<Message> messageList = messageRepository.findAllByChannelId(channelId);

        if (messageList.isEmpty()) {
            return Collections.emptyList();
        }
        List<FoundMessagesResponse> responses = new ArrayList<>();
        for (Message message : messageList) {
            responses.add(new FoundMessagesResponse(
                    message.getId(),
                    message.getCreatedAt(),
                    message.getUpdatedAt(),
                    message.getContent(),
                    message.getChannel().getId(),
                    message.getAuthor().getId(),
                    message.getAttachments().stream().map(BaseEntity::getId).toList()
            ));
        }
        return responses;
    }


    // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    // public 방일경우 작성을 해도 readstatus가 없음 최소 1회는 등록을 해야 하고 유저가 방에 있는지 확인 가능한 로직이 필요함
    // binary content
    @Override
    public MessageAttachmentsCreateResponse createMessage(
            MessageCreateRequest request,
            List<MultipartFile> fileList) {


        Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new NoSuchElementException("channel with id " + request.channelId() + " not found"));
        User user = userRepository.findById(request.authorId()).orElseThrow(() -> new NoSuchElementException("author with id " + request.authorId() + " not found"));

        boolean isReadStatusExist = readStatusRepository.existsByUserAndChannel(user, channel);
        System.out.println("isReadStatusExist = " + isReadStatusExist);

        if (!isReadStatusExist) {
            ReadStatus readStatus = ReadStatus.builder()
                    .user(user)
                    .channel(channel)
                    .build();
            readStatusRepository.save(readStatus);
        }

        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)
        List<BinaryContent> attachments = new ArrayList<>();
        if (hasValue(fileList)) {
            for (MultipartFile file : fileList) {
                // BinaryContent 생성
                BinaryContent attachment;
                try {
                    BinaryContent binaryContent = BinaryContent.builder()
                            .fileName(file.getOriginalFilename())
                            .size((long) file.getBytes().length)
                            .contentType(file.getContentType())
                            .bytes(file.getBytes())
                            .extension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))
                            .build();
                    attachment = binaryContentRepository.save(binaryContent);
                    System.out.println("attachment = " + attachment);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // 사진 저장
                String uploadFilePath = fileUploadUtils.getUploadPath(ATTACHMENT_PATH);

                String originalFileName = attachment.getFileName();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String newFileName = attachment.getId() + extension;

                File attachmentFile = new File(uploadFilePath, newFileName);

                try (FileOutputStream fos = new FileOutputStream(attachmentFile)) {
                    fos.write(attachment.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("attachment not saved", e);
                }
                attachments.add(attachment);
            }
        }

        // 메세지 생성
        Message message = Message.builder()
                .author(user)
                .channel(channel)
                .attachments(attachments)
                .content(request.content())
                .build();
        messageRepository.save(message);

        // body
        MessageAttachmentsCreateResponse messageAttachmentsCreateResponse = new MessageAttachmentsCreateResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                message.getAuthor().getId(),
                message.getAttachments().stream().map(BaseEntity::getId).toList());

        return messageAttachmentsCreateResponse;
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    }




    @Override
    public boolean deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new IllegalArgumentException("require message Id : BasicMessageService.deleteMessage"));
        messageRepository.deleteById(messageId);
        return true;
    }

    @Override
    public UpdateMessageResponse updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("message with id " + messageId + " not found"));
//
        message.setContent(request.newContent());
//
        UpdateMessageResponse response = new UpdateMessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(),
                message.getAuthor().getId(),
                message.getAttachments().stream().map(BaseEntity::getId).toList()
        );
        return response;
    }

    private boolean hasValue(List<MultipartFile> attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty()) && (attachmentFiles.get(0).getSize() != 0);
    }


    // not required
    @Override
    public Message findMessageById(UUID messageId) {
//        Objects.requireNonNull(messageId, "no messageId: BasicMessageService.findMessageById");
//        return Optional.ofNullable(messageRepository.findMessageById(messageId))
//                .orElseThrow(() -> new IllegalArgumentException("no message in DB: BasicMessageService.findMessageById"));
        return null;
    }

    // not required
    @Override
    public List<Message> findAllMessages() {
//        return messageRepository.findAllMessages();
        return null;
    }
}
