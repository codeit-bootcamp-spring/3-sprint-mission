package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.helper.FileUploadUtils;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final String ATTACHMENT_PATH = "img";
    private final FileUploadUtils fileUploadUtils;
    private final JpaMessageRepository messageRepository;
    private final JpaChannelRepository channelRepository;
    private final JpaUserRepository userRepository;
    private final JpaBinaryContentRepository binaryContentRepository;
    private final JpaReadStatusRepository readStatusRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public MessageResponse findAllByChannelId(UUID channelId, Pageable pageable) {
        List<Message> messagePage = messageRepository.findAllByChannelIdOrderByCreatedAt(channelId, pageable);

        long totalMessages = messageRepository.count();
        int totalPages = (int) Math.ceil((double) totalMessages / pageable.getPageSize());
        boolean hasNext = pageable.getPageNumber() + 1 < totalPages;

        List<String> contents = messagePage.stream().map(Message::getContent).collect(Collectors.toList());

        MessageResponse response = new MessageResponse(
                contents,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                hasNext,
                totalMessages
        );

        return response;
    }


    // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    // public 방일경우 작성을 해도 readstatus가 없음 최소 1회는 등록을 해야 하고 유저가 방에 있는지 확인 가능한 로직이 필요함
    // binary content
    @Override
    public JpaMessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> fileList) {

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
//                            .bytes(file.getBytes())
                            .extension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))
                            .build();
                    attachment = binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(),file.getBytes());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // 사진 저장
//                String uploadFilePath = fileUploadUtils.getUploadPath(ATTACHMENT_PATH);
//
//                String originalFileName = attachment.getFileName();
//                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
//                String newFileName = attachment.getId() + extension;
//
//                File attachmentFile = new File(uploadFilePath, newFileName);
//
//                try (FileOutputStream fos = new FileOutputStream(attachmentFile)) {
//                    fos.write(attachment.getBytes());
//                } catch (IOException e) {
//                    throw new RuntimeException("attachment not saved", e);
//                }
                attachments.add(attachment);
                try {
                    binaryContentStorage.put(attachment.getId(), file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

        JpaMessageResponse response = messageMapper.toDto(message);

        return response;
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
        Optional.ofNullable(messageId).orElseThrow(() -> new IllegalArgumentException("require message Id : BasicMessageService.deleteMessage"));
        messageRepository.deleteById(messageId);
        return true;
    }

    @Override
    public JpaMessageResponse updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException("message with id " + messageId + " not found"));
        message.setContent(request.newContent());

        JpaMessageResponse response = messageMapper.toDto(message);
        return response;
    }

    private boolean hasValue(List<MultipartFile> attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty()) && (attachmentFiles.get(0).getSize() != 0);
    }
}
