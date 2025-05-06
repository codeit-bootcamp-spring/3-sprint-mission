package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("해당 작성자가 존재하지 않습니다.");
        }

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(req -> {
                    BinaryContent binaryContent = new BinaryContent(
                            req.fileName(),
                            (long) req.data().length,
                            req.contentType(),
                            req.data()
                    );
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .collect(Collectors.toList());

        Message message = new Message(
                messageCreateRequest.content(),
                channelId,
                authorId,
                attachmentIds
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));
        message.update(request.content());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }
}