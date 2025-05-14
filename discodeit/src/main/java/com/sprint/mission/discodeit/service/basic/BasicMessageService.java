package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.AddBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    // @RequiredArgsConstructor로 대체되었다.
//    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository) {
//        this.messageRepository = messageRepository;
//        this.channelRepository = channelRepository;
//        this.userRepository = userRepository;
//    }

    @Override
    public Message create(CreateMessageRequest request, List<AddBinaryContentRequest> contentRequests) {
        if(request.channelId() == null || request.channelId().equals(UUID.randomUUID())){
            throw new IllegalArgumentException("존재하지 않는 채널입니다!");
        }
        if(request.authorId() == null || request.authorId().equals(UUID.randomUUID())){
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        List<UUID> attachmentIds = contentRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    BinaryContentType contentType = attachmentRequest.type();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, (long)bytes.length, bytes);
                    BinaryContent cratesBinaryContent = binaryContentRepository.save(binaryContent);
                    return binaryContentRepository.save(binaryContent).getId();
                }).toList();

        Message message = new Message(request.content(),request.channelId(),request.authorId(),attachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public MessageDTO find(UUID messageId) {
        return messageRepository.findById(messageId)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<MessageDTO> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Message update(UUID messageId, UpdateMessageRequest updateMessageRequest) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        if(updateMessageRequest.newContent() == null || updateMessageRequest.newContent().isBlank()){
            throw new IllegalArgumentException("빈 메시지는 전송할 수 없습니다.");
        }
        message.update(updateMessageRequest.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                        .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
        messageRepository.deleteById(messageId);
        System.out.println("delete message : " + messageId + " success.");
    }

    public MessageDTO toDTO(Message message){
        List<UUID> attachmentIds = message.getAttachmentIds();
        if(attachmentIds.isEmpty()){
            attachmentIds = new ArrayList<>();
        }

        return new MessageDTO(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                attachmentIds
        );
    }

}
