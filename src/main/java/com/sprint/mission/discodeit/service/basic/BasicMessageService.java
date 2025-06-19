package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;
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
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author with id " + authorId + " does not exist");
        }
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        User user = userRepository.findById(authorId).orElseThrow();

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
            .map(attachmentRequest -> {
                String fileName = attachmentRequest.fileName();
                String contentType = attachmentRequest.contentType();
                byte[] bytes = attachmentRequest.bytes();

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentStorage.put(binaryContent.getId(), attachmentRequest.bytes());
                return binaryContentRepository.save(binaryContent);
            })
            .toList();

        String content = messageCreateRequest.content();
        Message message = new Message(
            content,
            channel,
            user,
            attachments
        );
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageDto> findAllByChannelIdWithAuthor(UUID channelId, Pageable pageable) {
        Slice<MessageDto> slice = messageRepository.findAllByChannelIdWithAuthor(channelId, pageable)
                .map(messageMapper::toDto);

        return pageResponseMapper.fromSlice(slice);
    }

    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Override
    @Transactional
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachments()
            .stream()
            .map(BinaryContent::getId)
            .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}
