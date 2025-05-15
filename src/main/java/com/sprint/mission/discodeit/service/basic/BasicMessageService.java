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
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    // 첨부 파일
    private final BinaryContentRepository binaryContentRepository;


    // 리펙토링


    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        // 채널과 사용자 존재 여부 확인
        if (!channelRepository.existsById(messageCreateRequest.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id " + messageCreateRequest.getChannelId());
        }
        if (!userRepository.existsById(messageCreateRequest.getAuthorId())) {
            throw new NoSuchElementException("User not found with id " + messageCreateRequest.getAuthorId());
        }

        // 선택적 첨부파일 여부 처리
        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(mcr -> binaryContentRepository.save(new BinaryContent(
                        mcr.getFileName(),
                        mcr.getFileData(),
                        mcr.getFileType(),
                        mcr.getFileData().length
                )).getId())
                        .collect(Collectors.toList());

        // 생성
        Message message = new Message(
                messageCreateRequest.getMessageContent(),
                messageCreateRequest.getChannelId(),
                messageCreateRequest.getAuthorId(),
                attachmentIds
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        // 채널 유효성
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }

        return messageRepository.findAll().stream()
                // 특정 채널의 속하는 message 목록 추리기
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
        // 유효성
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // Update
        message.update(messageUpdateRequest.getMessageContent());

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        // 유효성
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // Cascade ( BinaryContent )
        // 해당 첨부 파일의 ID 가져오기
        for (UUID attachmentId : message.getAttachmentIds()) {
            // BinaryContent Delete
            binaryContentRepository.deleteById(attachmentId);
        }

        // Message Delete
        messageRepository.deleteById(messageId);
    }
}