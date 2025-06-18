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
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    //
    private final BinaryContentStorage binaryContentStorage;
    private final PageResponseMapper pageResponseMapper;


    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
            .orElseThrow(() -> new NoSuchElementException(
                "Channel with id " + messageCreateRequest.channelId() + " not found"));

        User author = userRepository.findById(messageCreateRequest.authorId())
            .orElseThrow(() -> new NoSuchElementException(
                "User with id " + messageCreateRequest.authorId() + " not found"));

        Message message = new Message(messageCreateRequest.content(), channel, author);

        for (BinaryContentCreateRequest request : binaryContentCreateRequests) {
            BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType()
            );

            binaryContentStorage.put(binaryContent.getId(), request.bytes());

            message.getAttachments().add(binaryContent);
        }

        Message saved = messageRepository.save(message);
        return messageMapper.toDto(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageMapper.toDto(message);
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
        // 유효성
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // Update
        message.update(request.newContent());

        return messageMapper.toDto(message);
    }

    @Override
    public void delete(UUID messageId) {
        // 유효성
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        messageRepository.delete(message);
    }
}