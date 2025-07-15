package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.PageResponse;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channelException.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.messageException.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.userException.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.jpa.JpaBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaMessageRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public PageResponse findAllByChannelIdAndCursor(UUID channelId, Instant cursor, Pageable pageable) {

        List<Message> messages = messageRepository
            .findSliceByCursor(channelId, cursor, pageable);

        boolean hasNext = messages.size() > pageable.getPageSize();
        if (hasNext) {
            messages = messages.subList(0, pageable.getPageSize());
        }
        Instant nextCursor = hasNext ? messages.get(messages.size() - 1).getCreatedAt() : null;

        List<MessageResponse> dtoList = messages.stream()
            .map(messageMapper::toDto)
            .toList();

        return PageResponse.builder()
            .content(dtoList)
            .nextCursor(nextCursor)
            .size(dtoList.size())
            .hasNext(hasNext)
            .totalElements(messageRepository.countByChannelId(channelId))
            .build();
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
    public MessageResponse createMessage(MessageCreateRequest request, List<MultipartFile> fileList) {
        Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId",request.channelId())));
        User user = userRepository.findById(request.authorId()).orElseThrow(() -> new UserNotFoundException(Map.of("userId",request.authorId())));

        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)
        List<BinaryContent> attachments = new ArrayList<>();
        if (hasValue(fileList)) {
            for (MultipartFile file : fileList) {

                // BinaryContent 생성
                BinaryContent attachment;
                try {
                    String originalFilename = file.getOriginalFilename();
                    String extension = "";

                    int dotIndex = originalFilename.lastIndexOf(".");
                    if (dotIndex != -1 && dotIndex < originalFilename.length() - 1) {
                        extension = originalFilename.substring(dotIndex);
                    } else {
                        extension = "";
                    }
                    BinaryContent binaryContent = BinaryContent.builder()
                        .fileName(originalFilename)
                        .size((long) file.getBytes().length)
                        .contentType(file.getContentType())
                        .extension(extension)
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

        MessageResponse response = messageMapper.toDto(message);

        return response;
        // for(BinaryContent 생성 -> 이미지 저장 -> BinaryContent Id 리스트로 저장)  -> 메세지 생성
    }

    @Override
    public void deleteMessage(UUID messageId) {
        if(!messageRepository.existsById(messageId)) {
            throw new MessageNotFoundException(Map.of("messageId",messageId));
        }
        messageRepository.deleteById(messageId);
    }

    @Override
    public MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
        message.setContent(request.newContent());

        MessageResponse response = messageMapper.toDto(message);
        return response;
    }

    private boolean hasValue(List<MultipartFile> attachmentFiles) {
        return (attachmentFiles != null) && (!attachmentFiles.isEmpty()) && (attachmentFiles.get(0).getSize() != 0);
    }
}
