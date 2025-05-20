package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.BinaryContent; // BinaryContent 사용시
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository; // 사용자 존재 확인용
import com.sprint.mission.discodeit.repository.ChannelRepository; // 채널 존재 확인용
import com.sprint.mission.discodeit.repository.BinaryContentRepository; // 첨부파일 삭제용
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository; // 의존성 주입
    private final ChannelRepository channelRepository; // 의존성 주입
    private final BinaryContentRepository binaryContentRepository; // 의존성 주입


    @Override
    public Message createMessage(MessageCreateRequest request) {
        // 채널 및 사용자 존재 여부 확인 (필요시)
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + request.channelId()));
        userRepository.findById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.authorId()));

        // 첨부파일 ID 유효성 검사 (필요시 BinaryContentRepository 사용)
        if (request.attachmentIds() != null) {
            request.attachmentIds().forEach(attachmentId -> {
                binaryContentRepository.findById(attachmentId)
                        .orElseThrow(() -> new NoSuchElementException("Attachment not found with id: " + attachmentId));
            });
        }

        Message message = new Message(request.content(), request.authorId(), request.channelId());
        Message createdMessage = messageRepository.save(message);
        
        if (request.attachmentIds() != null) {
            request.attachmentIds().forEach(attachmentId -> {
                BinaryContent binaryContent = binaryContentRepository.findById(attachmentId)
                        .orElseThrow(() -> new NoSuchElementException("Attachment not found with id: " + attachmentId));
                binaryContent.setId(createdMessage.getMessageId());
            });
        }

        // message.setMessageId(UUID.randomUUID());
        // message.setChannelId(request.channelId());
        // message.setAuthorId(request.authorId());
        // message.setContent(request.content());
        // message.setAttachmentIds(request.attachmentIds() == null ? new ArrayList<>() : new ArrayList<>(request.attachmentIds())); // null 방지 및 변경 가능한 리스트로 복사

        return createdMessage;
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + channelId));
        return messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channelId);
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found with id: " + messageId));
    }


    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message message = findMessageById(messageId);

        message.updateContent(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findMessageById(messageId);

        List<UUID> attachmentIds = message.getAttachmentIds();
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            attachmentIds.forEach(binaryContentRepository::deleteById); // 개별 삭제
        }
        messageRepository.deleteById(messageId);
    }
}
