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

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (channelRepository.loadById(channelId) == null) {
            throw new IllegalArgumentException("[Channel] 유효하지 않은 채널 (channelId=" + channelId + ")");
        }
        if (userRepository.loadById(authorId) == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 사용자 (userId=" + authorId + ")");
        }

        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = BinaryContent.of(fileName, (long) bytes.length,
                            contentType, bytes);
                    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
                    return createdBinaryContent.getId();
                })
                .toList();

        String content = messageCreateRequest.content();
        Message message = Message.of(authorId, channelId, content, attachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public Message get(UUID id) {
        return messageRepository.loadById(id);
    }

    @Override
    public List<Message> getByChannel(UUID channelId) {
        return messageRepository.loadByChannelId(channelId);
    }

    @Override
    public List<Message> getAll() { return messageRepository.loadAll(); }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        String content = messageUpdateRequest.content();
        Message message = messageRepository.loadById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("[Message] 유효하지 않은 메시지입니다. (messageId=" + messageId + ")");
        }
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        messageRepository.deleteById(id);
    }
}
