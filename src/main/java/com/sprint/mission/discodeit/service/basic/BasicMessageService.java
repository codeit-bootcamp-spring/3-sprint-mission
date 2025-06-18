package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaPageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.advanced.AdvancedMessageMapper;
import com.sprint.mission.discodeit.mapper.original.PageResponseMapper;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
@Transactional
@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final JpaMessageRepository messageRepository;
    private final JpaChannelRepository channelRepository;
    private final JpaUserRepository userRepository;
    private final JpaBinaryContentRepository binaryContentRepository;
//    private final JpaReadStatusRepository readStatusRepository;
//    private final MessageMapper messageMapper;
    private final AdvancedMessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;
//    private final PageResponseMapper pageResponseMapper;


    @Override
    public AdvancedJpaPageResponse findAllByChannelIdAndCursor(UUID channelId, Instant cursor, Pageable pageable) {

        List<Message> messages;
        if(cursor == null) {
//            messages = messageRepository.findAllByChannelIdOrderByCreatedAt(channelId, pageable);
            messages = messageRepository.findAllByChannelId(channelId, pageable);
        }else {
            messages = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(channelId, cursor, pageable);
        }
        boolean hasNext = messages.size() > pageable.getPageSize();
        Long totalElements = messageRepository.countByChannelId(channelId);
        Instant nextCursor = null;
        if(hasNext) {
            messages = messages.subList(0, pageable.getPageSize());
            nextCursor = messages.get(messages.size() - 1).getCreatedAt();
        }

        List<JpaMessageResponse> messagesDto = messages.stream().map(message -> messageMapper.toDto(message)).toList();

        AdvancedJpaPageResponse response = AdvancedJpaPageResponse.builder()
                .content(messagesDto)
                .nextCursor(nextCursor)
                .size(messages.size())
                .hasNext(hasNext)
                .totalElements(totalElements)
                .build();
        return response;
    }

    // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    // public 방일경우 작성을 해도 readstatus가 없음 최소 1회는 등록을 해야 하고 유저가 방에 있는지 확인 가능한 로직이 필요함
    // binary content
    @Override
    public JpaMessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> fileList) {

        Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new NoSuchElementException("channel with id " + request.channelId() + " not found"));
        User user = userRepository.findById(request.authorId()).orElseThrow(() -> new NoSuchElementException("author with id " + request.authorId() + " not found"));

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
                            .extension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")))
                            .build();
                    attachment = binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(),file.getBytes());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

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
