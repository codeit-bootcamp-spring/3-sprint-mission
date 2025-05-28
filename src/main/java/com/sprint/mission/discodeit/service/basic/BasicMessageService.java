package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message createMessage(MessageCreateRequest messageCreateRequest,
        List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }

        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("존재하지 않는 유저 ID입니다.");
        }

        List<UUID> attachmentIds = new ArrayList<>();
        if (binaryContentCreateRequests != null) {
            for (BinaryContentCreateRequest fileRequest : binaryContentCreateRequests) {
                if (!fileRequest.isValid()) {
                    throw new IllegalArgumentException("메세지에 첨부파일을 추가할 수 없습니다. 파일을 확인해주세요.");
                }

                BinaryContent binaryContent = new BinaryContent(
                    fileRequest.fileName(),
                    authorId,
                    fileRequest.bytes(),
                    fileRequest.contentType()
                );
                BinaryContent saved = binaryContentRepository.save(binaryContent);
                attachmentIds.add(saved.getId());
            }
        }
        Message message = new Message(
            messageCreateRequest.content(),
            channelId,
            authorId,
            attachmentIds
        );

        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest request) {
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        msg.updateContent(request.newContent());
        messageRepository.save(msg);
        return msg;
    }

    @Override
    public void deleteMessage(UUID messageId, UUID senderId) {
        Message msg = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        if (!msg.getAuthorId().equals(senderId)) {
            throw new SecurityException("해당 메시지를 삭제할 권한이 없습니다.");
        }

        for (UUID attachmentId : msg.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }

        messageRepository.delete(messageId);
    }
}
