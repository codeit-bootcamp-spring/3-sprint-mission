package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    @Override
    public MessageResponse create(
        MessageCreateRequest request,
        List<BinaryContentCreateRequest> attachments
    ) {
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Channel with id %s does not exist".formatted(request.channelId()))
            );

        User author = userRepository.findById(request.authorId())
            .orElseThrow(() ->
                new NoSuchElementException(
                    "User with id %s does not exist".formatted(request.authorId()))
            );

        List<BinaryContent> attachmentEntities = attachments.stream()
            .map(file -> {
                BinaryContent content = new BinaryContent(
                    file.fileName(),
                    (long) file.bytes().length,
                    file.contentType()
                );
                BinaryContent saved = binaryContentRepository.save(content);
                binaryContentStorage.put(saved.getId(), file.bytes());
                return saved;
            })
            .toList();

        Message saved = messageRepository.save(
            new Message(request.content(), channel, author, attachmentEntities));
        return messageMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponse find(UUID messageId) {
        return messageRepository.findById(messageId)
            .map(messageMapper::toResponse)
            .orElseThrow(() ->
                new NoSuchElementException("Message with id %s not found".formatted(messageId))
            );
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageResponse> findAllByChannelId(
        UUID channelId,
        Instant createdAt,
        Pageable pageable
    ) {
        Instant cursor = Optional.ofNullable(createdAt).orElse(Instant.now());

        Slice<MessageResponse> responseSlice = messageRepository
            .findAllByChannelIdWithAuthor(channelId, cursor, pageable)
            .map(messageMapper::toResponse);

        Instant nextCursor = responseSlice.isEmpty()
            ? null
            : responseSlice.getContent().get(responseSlice.getContent().size() - 1).createdAt();

        return pageResponseMapper.fromSlice(responseSlice, nextCursor);
    }

    @Transactional
    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() ->
                new NoSuchElementException("Message with id %s not found".formatted(messageId))
            );

        message.update(request.newContent());
        return messageMapper.toResponse(message);
    }

    @Transactional
    @Override
    public MessageResponse delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() ->
                new NoSuchElementException("Message with id %s not found".formatted(messageId))
            );

        messageRepository.delete(message);
        return messageMapper.toResponse(message);
    }
}
