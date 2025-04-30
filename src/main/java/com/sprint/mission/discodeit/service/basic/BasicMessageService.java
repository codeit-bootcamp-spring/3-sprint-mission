package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

    @Override
    public MessageCreateResponse create(MessageCreateRequest createRequest) {
        /* 유저가 해당 채널에 있는지 validation check */

        this.channelRepository.findById(createRequest.channelId())
                .ifPresent((channel) -> {
                            if (!channel.getAttendees().contains(createRequest.userId())) {
                                new Exception("유저가 참여하지 않은 채팅방에는 메세지를 보낼수 없습니다.");
                            }
                        }
                );

        /* 선택적으로 여러개의 첨부파일 같이 등록 가능 */
        Message message = new Message(createRequest);

        this.messageRepository.save(message);

        /* 채널 lastMessageTime 업데이트 */
        this.channelRepository.findById(createRequest.channelId()).ifPresent((channel) -> {
            channel.setLastMessageTime(Instant.now());
            this.channelRepository.save(channel);
        });
        return new MessageCreateResponse(message);
    }

    @Override
    public MessageCreateResponse findById(UUID messageId) {
        Message message = this.messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return new MessageCreateResponse(message);
    }

    @Override
    public List<MessageCreateResponse> findAllByChannelId(UUID channelId) {

        List<MessageCreateResponse> messages = this.messageRepository
                .findAll()
                .stream().filter((message) -> {
                    return message.getChannelId().equals(channelId);
                }).map(MessageCreateResponse::new).toList();


        return messages;
    }

    @Override
    public MessageCreateResponse update(MessageUpdateRequest updateRequest) {
        Message message = this.messageRepository.findById(updateRequest.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + updateRequest.messageId() + " not found"));

        message.update(updateRequest.newContent(), updateRequest.attachmentIds());

        /* 업데이트 후 다시 DB 저장 */
        this.messageRepository.save(message);

        Message updatedMessage = this.messageRepository.findById(updateRequest.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + updateRequest.messageId() + " not found"));

        return new MessageCreateResponse(updatedMessage);
    }

    @Override
    public void delete(UUID messageId) {
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
