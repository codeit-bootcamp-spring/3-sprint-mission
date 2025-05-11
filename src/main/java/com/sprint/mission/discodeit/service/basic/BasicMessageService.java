package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundMessageException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("basicMessageService")
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageRequestDTO messageRequestDTO, List<BinaryContentDTO> binaryContentDTOS) {
        User user = findUser(messageRequestDTO.senderId());
        Channel channel = findChannel(messageRequestDTO.channelId());

        // Repository 저장용 데이터
        List<BinaryContent> binaryContents = convertBinaryContentDTOS(binaryContentDTOS);
        // BinaryContent -> UUID
        List<UUID> attachmentIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .toList();

        Message message = MessageRequestDTO.toEntity(messageRequestDTO);
        message.updateAttachmentIds(attachmentIds);

        // 메시지를 보낸 user의 mesagesList에 해당 메시지 추가
        if (!user.getChannels().contains(channel.getId())) {
            throw new UserNotInChannelException();
        } else {
            user.getMessages().add(message.getId());
            userRepository.save(user);
        }

        // 메시지를 보낸 channel의 mesagesList에 해당 메시지 추가
        channel.getMessages().add(message.getId());
        channelRepository.save(channel);

        for (BinaryContent binaryContent : binaryContents) {
            binaryContentRepository.save(binaryContent);
        }
        messageRepository.save(message);

        return message;
    }

    @Override
    public MessageResponseDTO findById(UUID messageId) {
        Message message = findMessage(messageId);

        return MessageResponseDTO.toDTO(message);
    }

    @Override
    public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {
        Channel channel = findChannel(channelId);

        List<MessageResponseDTO> channelMessages = channel.getMessages().stream()
                .map(messageId -> {
                    Message message = findMessage(messageId);
                    return MessageResponseDTO.toDTO(message);
                })
                .toList();

        return channelMessages;
    }

    @Override
    public List<MessageResponseDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(MessageResponseDTO::toDTO)
                .toList();
    }

    @Override
    public List<MessageResponseDTO> findAllByUserId(UUID userId) {
        User user = findUser(userId);

        List<MessageResponseDTO> userMessages = user.getMessages().stream()
                .map(messageId -> {
                    Message message = findMessage(messageId);
                    return MessageResponseDTO.toDTO(message);
                })
                .toList();

        return userMessages;
    }

    @Override
    public List<MessageResponseDTO> findAllByContainingWord(String word) {
        return messageRepository.findMessageByContainingWord(word).stream()
                .map(MessageResponseDTO::toDTO)
                .toList();
    }

    @Override
    public MessageResponseDTO updateBinaryContent(UUID messageId, List<BinaryContentDTO> binaryContentDTOS) {
        Message message = findMessage(messageId);
        // 기존 BinaryContent 제거
        List<UUID> originAttachmentsIds = message.getAttachmentIds();
        for (UUID id : originAttachmentsIds) {
            binaryContentRepository.deleteById(id);
        }

        // Repository 저장용 데이터
        List<BinaryContent> binaryContents = convertBinaryContentDTOS(binaryContentDTOS);
        // BinaryContent -> UUID
        List<UUID> attachmentIds = binaryContents.stream()
                .map(BinaryContent::getId)
                .toList();

        message.updateAttachmentIds(attachmentIds);

        for (BinaryContent binaryContent : binaryContents) {
            binaryContentRepository.save(binaryContent);
        }
        messageRepository.save(message);

        return MessageResponseDTO.toDTO(message);
    }

    @Override
    public MessageResponseDTO updateContent(UUID messageId, String content) {
        Message message = findMessage(messageId);

        message.updateContent(content);

        messageRepository.save(message);

        return MessageResponseDTO.toDTO(message);
    }

    @Override
    public void deleteById(UUID messageId) {
        Message message = findMessage(messageId);

        // User의 메시지 목록에서 삭제
        userRepository.findAll().forEach(user -> {
            if (user.getMessages().removeIf(id -> id.equals(messageId))) {
                userRepository.save(user);
            }
        });

        // Channel의 메시지 목록에서 삭제
        channelRepository.findAll().forEach(channel -> {
            if (channel.getMessages().removeIf(id -> id.equals(messageId))) {
                channelRepository.save(channel);
            }
        });

        for (UUID binaryContentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(binaryContentId);
        }
        messageRepository.deleteById(messageId);
    }

    private List<BinaryContent> convertBinaryContentDTOS(List<BinaryContentDTO> binaryContentDTOS) {
        return binaryContentDTOS.stream()
                .map(BinaryContentDTO::toEntity)
                .toList();
    }

    private Message findMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(NotFoundMessageException::new);
    }

    private Channel findChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(NotFoundChannelException::new);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }
}
