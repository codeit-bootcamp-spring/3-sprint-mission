package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    @Override
    public Message create(MessageCreateRequest createRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) throws IllegalAccessException {
        if (!channelRepository.existsById(createRequest.channelId())) {
            throw new NoSuchElementException("Channel with id " + createRequest.channelId() + " does not exist");
        }
        if (!userRepository.existsById(createRequest.userId())) {
            throw new NoSuchElementException("Author with id " + createRequest.userId() + " does not exist");
        }

        /* 유저가 해당 채널에 있는지 validation check */
        List<UUID> attendeeIds = this.readStatusRepository.findAllByChannelId(createRequest.channelId()).stream().map((ReadStatus::getUserId)).toList();

        if (!attendeeIds.contains(createRequest.userId())) {
            throw new IllegalAccessException("유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다.");
        }
        /* 첨부 파일 생성, 선택적으로 여러개의 첨부파일 같이 등록 가능 */
        List<UUID> attachmentIds = binaryContentCreateRequests.stream().map((attachmentRequest) -> {
            BinaryContent binaryContent = new BinaryContent(attachmentRequest.fileName(), (long) attachmentRequest.bytes().length, attachmentRequest.contentType(), attachmentRequest.bytes());
            BinaryContent createdBinaryContent = this.binaryContentRepository.save(binaryContent);
            return createdBinaryContent.getId();
        }).toList();

        Message message = new Message(createRequest.content(), createRequest.userId(), createRequest.channelId(), attachmentIds);

        /* 채널 lastMessageAt 업데이트 */
        this.channelRepository.findById(createRequest.channelId()).ifPresent((channel) -> {
            channel.setLastMessageAt(Instant.now());
            this.channelRepository.save(channel);
        });

        return this.messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        return this.messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.messageRepository
                .findAll()
                .stream().filter((message) -> {
                    return message.getChannelId().equals(channelId);
                }).toList();

    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest updateRequest) {
        Message message = this.messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.update(updateRequest.newContent(), updateRequest.attachmentIds());

        /* 업데이트 후 다시 DB 저장 */
        this.messageRepository.save(message);

        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

    }

    @Override
    public void delete(UUID messageId) {
        // Message 객체 사용해야하므로 가져옴
        Message message = this.messageRepository.findById(messageId).
                orElseThrow(() -> new NoSuchElementException("Message with messageId " + messageId + " not found"));

        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            for (UUID binaryContentId : message.getAttachmentIds()) {
                this.binaryContentRepository.deleteById(binaryContentId);
            }
        }

        this.messageRepository.deleteById(messageId);
    }

}
