package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
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
@Transactional
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDto create(MessageCreateRequest req, List<BinaryContentCreateRequest> files) {
        User author = userRepository.findById(req.authorId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저는 존재하지 않습니다."));

        Channel channel = channelRepository.findById(req.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));

        List<BinaryContent> attachments = files.stream()
                .map(attachmentRequest -> binaryContentRepository.save(
                        new BinaryContent(attachmentRequest.fileName(), (long) attachmentRequest.bytes().length,
                                attachmentRequest.contentType(), attachmentRequest.bytes())))
                .toList();

        Message message = new Message(req.content(), channel, author, attachments);
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public MessageDto find(UUID messageId) {
        return messageMapper.toDto(
                messageRepository.findById(messageId)
                        .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지는 없습니다."))
        );
    }

    @Override
    public List<MessageDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지은 없습니다."));
        message.update(request.newContent());
        return messageMapper.toDto(messageRepository.save(message));
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 메시지은 없습니다."));
        binaryContentRepository.deleteAll(message.getAttachments());
        messageRepository.deleteById(messageId);
    }
}