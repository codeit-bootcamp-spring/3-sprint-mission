package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
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
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageDto create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        Channel channel = channelRepository.findById(messageCreateRequest.channelId())
            .orElseThrow(() -> {
                return new NoSuchElementException("Channel with id " + messageCreateRequest.channelId() + " does not exist");
            });

        User author = userRepository.findById(messageCreateRequest.authorId())
            .orElseThrow(() -> {
                return new NoSuchElementException("Author with id " + messageCreateRequest.authorId() + " does not exist");
            });

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    binaryContentRepository.save(binaryContent);
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

        return messageMapper.messageToMessageDto(message);
    }

    @Transactional
    @Override
    public MessageDto find(UUID messageId) {
        return messageRepository.findById(messageId)
            .map(messageMapper::messageToMessageDto)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Transactional
    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
            .map(messageMapper::messageToMessageDto)
            .toList();
    }

    @Transactional
    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageMapper.messageToMessageDto(message);
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachments()
                .forEach(attachment -> binaryContentRepository.deleteById(attachment.getId()));

        messageRepository.deleteById(messageId);
    }
}
