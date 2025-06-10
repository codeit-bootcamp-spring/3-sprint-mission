package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
        UUID channelId = request.channelId();
        UUID authorId = request.authorId();

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    "Channel with id " + channelId + " does not exist"));

        User author = userRepository.findById(authorId)
            .orElseThrow(
                () -> new NoSuchElementException("User with id " + authorId + " does not exist"));

        List<BinaryContent> attachmentEntities = attachments.stream()
            .map(file -> {
                try {
                    byte[] data = file.getBytes();

                    BinaryContent binaryContent = new BinaryContent(
                        file.getOriginalFilename(),
                        file.getSize(),
                        file.getContentType()
                    );

                    BinaryContent saved = binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(saved.getId(), data);

                    return saved;
                } catch (IOException e) {
                    throw new RuntimeException("File process failed: " + file.getOriginalFilename(),
                        e);
                }
            })
            .toList();

        Message message = new Message(request.content(), channel, author, attachmentEntities);
        return messageMapper.toDto(messageRepository.save(message));
    }


    @Transactional(readOnly = true)
    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));
        return messageMapper.toDto(message);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId, int page) {
        Pageable pageable = PageRequest.of(page, 50, Sort.by("createdAt").descending());
        return messageRepository.findAllByChannel_IdOrderByCreatedAtDesc(channelId, pageable)
            .map(messageMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.update(request.newContent());
        return messageMapper.toDto(message);
    }

    @Transactional
    @Override
    public MessageDto delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new NoSuchElementException("Message with id " + messageId + " not found"));

        messageRepository.delete(message);
        return messageMapper.toDto(message);
    }

}
