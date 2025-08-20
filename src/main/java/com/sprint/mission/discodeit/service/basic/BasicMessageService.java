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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(
        @Valid MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests
    ) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.error("채널 조회 실패 - channelId={}", channelId);
            return new ChannelNotFoundException(channelId);
            });

        User author = userRepository.findById(authorId)
            .orElseThrow(() -> {
            log.error("사용자 조회 실패 - authorId={}", authorId);
            return new UserNotFoundException(authorId);
            });

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(binaryContentCreateRequest -> {
            BinaryContent binaryContent = new BinaryContent(
                binaryContentCreateRequest.fileName(),
                (long) binaryContentCreateRequest.bytes().length,
                binaryContentCreateRequest.contentType()
            );

            BinaryContent saved = binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(saved.getId(), binaryContentCreateRequest.bytes());
            return saved;
            })
            .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
            content,
            channel,
            author,
            attachments
        );
        log.debug("메시지 entity 생성: {}", message);
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    public Message find(@NotNull UUID messageId) {
        return messageRepository.findById(messageId)
            .orElseThrow(() -> {
                log.error("메시지 조회 실패 - messageId={}", messageId);
                return new MessageNotFoundException(messageId);
            });
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(
        @NotNull UUID channelId,
        Instant cursor,
        Pageable pageable
    ) {
        int size = pageable.getPageSize();
        Pageable cursorPageable = PageRequest.of(0,size + 1, Sort.by("createdAt").descending());

        Slice<Message> slice;
        if (cursor == null) {
            slice = messageRepository.findAllByChannelId(channelId, cursorPageable);
        } else {
            slice = messageRepository.findAllByChannelId(channelId, cursor, cursorPageable);
        }

        List<MessageDto> messageDtos = slice
            .map(messageMapper::toDto)
            .getContent();

        //hasNext 계산
        boolean hasNext = messageDtos.size() > size;
        List<MessageDto> content = hasNext ? messageDtos.subList(0, size) : messageDtos;

        //nextCursor 계산
        Instant nextCursor = hasNext ? content.get(content.size() - 1).createdAt() : null;

        Slice<MessageDto> dtoSlice = new SliceImpl<>(content, pageable, hasNext);

        return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
    }

    @Override
    @PreAuthorize("@messageRepository.existsByIdAndAuthor_Id(#messageId, principal.userDto.id())")
    @Transactional
    public MessageDto update(@NotNull @P("messageId") UUID messageId, @Valid MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
              log.error("메시지 조회 실패 - messageId={}", messageId);
              return new MessageNotFoundException(messageId);
            });
        message.update(newContent);
        return messageMapper.toDto(message);
    }

    @Override
    @PreAuthorize("@messageRepository.existsByIdAndAuthor_Id(#messageId, principal.userDto.id())")
    @Transactional
    public void delete(@NotNull @P("messageId") UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> {
              log.error("메시지 조회 실패 - messageId={}", messageId);
              return new MessageNotFoundException(messageId);
            });

        binaryContentRepository.deleteAll(message.getAttachments());
        messageRepository.delete(message);
    }
}