package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessageRequest request) {
        // 1. Channel 유효성 검사
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }

        // 2. User 유효성 검사
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        // 3. Message 생성
        Message message = new Message(request.content(), request.channelId(), request.authorId());

        // 4. 첨부파일 처리
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            for (CreateBinaryContentRequest attachment : request.attachments()) {
                BinaryContent binaryContent = new BinaryContent(attachment.id(), attachment.fileName(), attachment.fileType(), attachment.content());
                // 첨부파일을 서버에 저장, 파일 ID 얻어옴
                binaryContentRepository.save(binaryContent);
                message.update(binaryContent.getId());
            }
        }

        // 5. 메시지 저장
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {

        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {

        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        return messages;
    }

    @Override
    public Message update(UpdateMessageRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.messageId() + " not found"));
        message.update(request.content());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        Message message = messageRepository.findById(messageId).get();
        List<UUID> ids = message.getAttachmentIds();
        for (UUID id : ids) {
            if (binaryContentRepository.existsById(id)) {
                binaryContentRepository.delete(id);
            }
        }
        messageRepository.deleteById(messageId);
    }
}
