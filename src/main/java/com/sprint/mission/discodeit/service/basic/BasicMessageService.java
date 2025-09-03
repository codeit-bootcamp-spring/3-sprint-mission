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
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
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
        log.info("[BasicMessageService] Creating message. [channelId={}] [authorId={}]",
            request.channelId(), request.authorId());

        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> {
                log.warn("[BasicMessageService] Channel not found. [channelId={}]",
                    request.channelId());
                return new NoSuchElementException(
                    "Channel with id %s does not exist".formatted(request.channelId()));
            });

        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> {
                log.warn("[BasicMessageService] Author not found. [authorId={}]",
                    request.authorId());
                return new NoSuchElementException(
                    "User with id %s does not exist".formatted(request.authorId()));
            });

        List<BinaryContent> attachmentEntities = attachments.stream()
            .map(file -> {
                BinaryContent content = new BinaryContent(file.fileName(),
                    (long) file.bytes().length, file.contentType());
                BinaryContent saved = binaryContentRepository.save(content);
                binaryContentStorage.put(saved.getId(), file.bytes());
                return saved;
            }).toList();

        Message saved = messageRepository.save(
            new Message(request.content(), channel, author, attachmentEntities));

        log.debug("[BasicMessageService] Message created. [id={}]", saved.getId());
        return messageMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageResponse find(UUID messageId) {
        log.info("[BasicMessageService] Finding message. [id={}]", messageId);

        return messageRepository.findById(messageId)
            .map(message -> {
                log.debug("[BasicMessageService] Message found. [id={}]", messageId);
                return messageMapper.toResponse(message);
            })
            .orElseThrow(() -> {
                log.warn("[BasicMessageService] Message not found. [id={}]", messageId);
                return new MessageNotFoundException(messageId);
            });

    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Instant createdAt,
        Pageable pageable) {
        log.info("[BasicMessageService] Finding messages by channel. [channelId={}] [cursor={}]",
            channelId, createdAt);

        Instant cursor = Optional.ofNullable(createdAt).orElse(Instant.now());

        Slice<MessageResponse> responseSlice = messageRepository
            .findAllByChannelIdWithAuthor(channelId, cursor, pageable)
            .map(messageMapper::toResponse);

        Instant nextCursor = responseSlice.isEmpty()
            ? null
            : responseSlice.getContent().get(responseSlice.getContent().size() - 1).createdAt();

        log.debug("[BasicMessageService] Messages found. [count={}] [channelId={}]",
            responseSlice.getContent().size(), channelId);
        return pageResponseMapper.fromSlice(responseSlice, nextCursor);
    }

    @Transactional
    @PreAuthorize("@messageSecurity.isOwner(#messageId, authentication.principal.userResponse.id())")
    @Override
    public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
        log.info("[BasicMessageService] Updating message. [id={}]", messageId);

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.warn("[BasicMessageService] Message not found for update. [id={}]", messageId);
                return new MessageNotFoundException(messageId);
            });

        message.update(request.newContent());

        log.debug("[BasicMessageService] Message updated. [id={}]", messageId);
        return messageMapper.toResponse(message);
    }

    @Transactional
    @PreAuthorize("@messageSecurity.isOwner(#messageId, authentication.principal.userResponse.id())")
    @Override
    public MessageResponse delete(UUID messageId) {
        log.info("[BasicMessageService] Deleting message. [id={}]", messageId);

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.warn("[BasicMessageService] Message not found for deletion. [id={}]",
                    messageId);
                return new MessageNotFoundException(messageId);
            });

        messageRepository.delete(message);

        log.debug("[BasicMessageService] Message deleted. [id={}]", messageId);
        return messageMapper.toResponse(message);
    }
}
