package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    //
    private final PageResponseMapper pageResponseMapper;


    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        log.debug("메세지 생성 시작 : request = {}", messageCreateRequest);

        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
            .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        User author = userRepository.findById(messageCreateRequest.authorId())
            .orElseThrow(() -> UserNotFoundException.withId(authorId));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
                String fileName = attachmentRequest.fileName();
                String contentType = attachmentRequest.contentType();
                byte[] bytes = attachmentRequest.bytes();

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
            content,
            channel,
            author,
            attachments
        );

        messageRepository.save(message);
        log.info("메세지 생성 완료 : id = {}, channelId = {}", message.getId(), channel);
        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId)
            .map(messageMapper::toDto)
            .orElseThrow(() -> MessageNotFoundException.withId(messageId));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createAt,
        Pageable pageable) {
        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId,
                Optional.ofNullable(createAt).orElse(Instant.now()),
                pageable)
            .map(messageMapper::toDto);

        Instant nextCursor = null;
        if (!slice.getContent().isEmpty()) {
            nextCursor = slice.getContent().get(slice.getContent().size() - 1)
                .createdAt();
        }

        return pageResponseMapper.fromSlice(slice, nextCursor);
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        log.debug("메세지 수정 시작 : id = {}, request = {}", messageId, request);
        // 유효성
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> MessageNotFoundException.withId(messageId));

        // Update
        message.update(request.newContent());

        log.info("메세지 수정 완료 : id = {}, channelId = {}", messageId, message.getChannel().getId());
        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        log.debug("메세지 삭제 시작 : id = {}", messageId);
        if (!messageRepository.existsById(messageId)) {
            throw MessageNotFoundException.withId(messageId);
        }
        messageRepository.deleteById(messageId);
        log.info("메세지 삭제 완료 : id = {}", messageId);
    }
}