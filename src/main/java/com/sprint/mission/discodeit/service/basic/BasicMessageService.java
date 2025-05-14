package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class BasicMessageService implements MessageService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto) {
        Message message = messageRepository.createMessage(messageCreateRequestDto);
        MessageResponseDto messageResponseDto;

        List<byte[]> messageFiles = messageCreateRequestDto.getMessageFile();
        if (messageFiles != null && !messageFiles.isEmpty()) {
            // 바이너리 생성 로직
            messageFiles.forEach(file -> {
                BinaryContentCreateRequestDto binaryContentCreateRequestDto = new BinaryContentCreateRequestDto(
                        message.getId(),
                        BinaryContentType.MESSAGE_ATTACHMENT,
                        BinaryOwnerType.MESSAGE,
                        file
                );
                binaryContentRepository.createBinaryContent(binaryContentCreateRequestDto);
            });
            messageResponseDto = new MessageResponseDto(message);
        } else {
            messageResponseDto = new MessageResponseDto(message);
        }
        return messageResponseDto;
    }

    @Override
    public List<MessageResponseDto> getChannelMessages(UUID channelId) {
        List<Message> messages = messageRepository.getChannelMessages(channelId);
        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();

        for (Message message : messages) {
            List<BinaryContent> messageBinaryContents = binaryContentRepository.findAllBinaryContentById(message.getId());

            List<byte[]> messageFiles = new ArrayList<>();
            for (BinaryContent binaryContent : messageBinaryContents) {
                messageFiles.add(binaryContent.getData());
            }

            MessageResponseDto dto = new MessageResponseDto(message, messageFiles);
            messageResponseDtos.add(dto);
        }

        return messageResponseDtos;
    }

    @Override
    public boolean updateMessage(MessageUpdateRequestDto messageUpdateRequestDto) {
        return messageRepository.updateMessage(messageUpdateRequestDto);
    }

    @Override
    public void deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto) {
        messageRepository.deleteMessage(messageDeleteRequestDto);
//        바이너리 삭제 로직
        binaryContentRepository.findAllBinaryContentById(messageDeleteRequestDto.getMessageId()).forEach(file -> {
            binaryContentRepository.deleteBinaryContentById(messageDeleteRequestDto.getMessageId());
        });
    }
}
