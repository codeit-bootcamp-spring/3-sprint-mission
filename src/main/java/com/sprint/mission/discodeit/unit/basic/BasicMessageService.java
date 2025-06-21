package com.sprint.mission.discodeit.unit.basic;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.AdvancedJpaPageResponse;
import com.sprint.mission.discodeit.dto.message.response.JpaMessageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.advanced.MessageMapper;
import com.sprint.mission.discodeit.repository.jpa.*;
import com.sprint.mission.discodeit.unit.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
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
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;



    @Override
    public AdvancedJpaPageResponse findAllByChannelIdAndCursor(UUID channelId, Instant cursor, Pageable pageable) {

        List<Message> messages = messages = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(channelId, cursor, pageable);

//        List<Message> messages;
//        if(cursor == null) {
//            messages = messageRepository.findAllByChannelId(channelId, pageable);
//        }
//        else {
//            messages = messageRepository.findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(channelId, cursor, pageable);
//        }

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

    /**
     * for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
     * public 방일경우 작성을 해도 readstatus가 없음 최소 1회는 등록을 해야 하고 유저가 방에 있는지 확인 가능한 로직이 필요함
     * binary content
     * @param request
     * @param fileList
     * @return
     */
    @Override
    public JpaMessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> fileList) {
        Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId",request.channelId())));
        User user = userRepository.findById(request.authorId()).orElseThrow(() -> new UserNotFoundException(Map.of("userId",request.authorId())));

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
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
        message.setContent(request.newContent());

        JpaMessageResponse response = messageMapper.toDto(message);
        return response;
    }

    private boolean hasValue(List<MultipartFile> attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty()) && (attachmentFiles.get(0).getSize() != 0);
    }
}
