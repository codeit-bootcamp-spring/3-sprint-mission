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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest messageCreateRequest,
                          List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID userId = messageCreateRequest.authorId();
        UUID channelId = messageCreateRequest.channelId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }

        String text = messageCreateRequest.text();
        List<UUID> attachments = binaryContentCreateRequests.stream()
                .map(contents -> {
                    String fileName = contents.fileName();
                    byte[] bytes = contents.bytes();
                    String contentType = contents.contentType();

                    BinaryContent binaryContent = new BinaryContent(fileName, contentType, bytes);
                    binaryContentRepository.save(binaryContent);
                    return binaryContent.getId();

                })
                .toList();

        Message message =
                Message.builder()
                        .currentChannelId(channelId)
                        .currentUserId(userId)
                        .text(text)
                        .attachmentIds(attachments)
                        .build();

        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .toList();
    }

    @Override
    public Message find(UUID id) {

        return messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
    };

    @Override
    public List<Message> findByText(String text) {

        return messageRepository.findByText(text)
                .stream()
                .toList();
    }

    @Override
    public Message update(UUID id, MessageUpdateRequest messageUpdateDTO) {
        String newText = messageUpdateDTO.newText();
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
        message.updateText(newText);

        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
        message.getAttachmentIds()
                        .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(id);

    }
}
