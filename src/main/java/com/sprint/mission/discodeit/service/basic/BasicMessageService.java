package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
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
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request,
                             List<BinaryContentCreateRequest> attachmentRequests) {

        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("User", ErrorMessages.ERROR_NOT_FOUND))
            );
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("Channel", ErrorMessages.ERROR_NOT_FOUND))
            );

        List<BinaryContent> attachments = attachmentRequests == null ? List.of() :
            attachmentRequests.stream()
                .map(req -> {
                    BinaryContent binaryContent = new BinaryContent(req.fileName(),
                        (long) req.bytes().length,
                        req.contentType());
                    BinaryContent savedAttachment = binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(savedAttachment.getId(), req.bytes());
                    return savedAttachment;
                })
                .toList();

        Message message = new Message(
            request.content(),
            channel,
            author,
            attachments
        );

        message.validateContent(attachments);
        Message savedMessage = messageRepository.save(message);

        return messageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("Message", ErrorMessages.ERROR_NOT_FOUND)
            ));

        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
                                                       Pageable pageable) {
        int pageSize = (pageable != null && pageable.isPaged()) ? pageable.getPageSize() : 50;

        PageRequest pageRequest = PageRequest.of(0, pageSize + 1,
            Sort.by("createdAt").descending());

        Slice<Message> messageSlice = (cursor != null)
            ? messageRepository.findAllByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            channelId, cursor, pageRequest)
            : messageRepository.findAllByChannelIdOrderByCreatedAtDesc(channelId, pageRequest);

        return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toDto));
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("Message", ErrorMessages.ERROR_NOT_FOUND)));

        message.update(newContent);
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        messageRepository.findById(messageId).orElseThrow(() -> new NoSuchElementException(
            ErrorMessages.format("Message", ErrorMessages.ERROR_NOT_FOUND)));

        messageRepository.deleteById(messageId);
    }
}
