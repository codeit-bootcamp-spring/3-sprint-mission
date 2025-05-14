package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto, BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        Message message = new Message(messageCreateRequestDto.getUserId(), messageCreateRequestDto.getMessageContent());
        messageRepository.createMessage(message, messageCreateRequestDto.getChannelId());

        if (binaryContentCreateRequestDto != null && binaryContentCreateRequestDto.getData() != null) {
            for (byte[] fileData : binaryContentCreateRequestDto.getData()) {
                BinaryContent binaryContent = new BinaryContent(
                        message.getId(),
                        binaryContentCreateRequestDto.getContentType(),
                        binaryContentCreateRequestDto.getFileContentType(),
                        fileData
                );
                binaryContentRepository.createBinaryContent(binaryContent);
            }
        }

        return new MessageResponseDto(message);
    }

    @Override
    public List<MessageResponseDto> getChannelMessages(UUID channelId) {
        List<Message> messages = messageRepository.getChannelMessages(channelId);
        List<MessageResponseDto> messageResponseDtos = new ArrayList<>();

        for (Message message : messages) {
            List<BinaryContent> messageBinaryContents = binaryContentRepository.findAllAttachmentsByOwnerId(message.getId());

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
        List<Message> messages = messageRepository.getChannelMessages(messageUpdateRequestDto.getChannelId());

        for (Message message : messages) {
            if (message.getId().equals(messageUpdateRequestDto.getMessageId())) {
                message.setContent(messageUpdateRequestDto.getMessageContent());
                return messageRepository.updateMessage(message, messageUpdateRequestDto.getChannelId());
            }
        }

        return false;
    }

    @Override
    public boolean deleteMessage(MessageDeleteRequestDto messageDeleteRequestDto) {
        Message message = null;
        List<Message> channelMessages = messageRepository.getChannelMessages(messageDeleteRequestDto.getChannelId());
        for (Message msg : channelMessages) {
            if (msg.getId().equals(messageDeleteRequestDto.getMessageId())) {
                message = msg;
                break;
            }
        }
        boolean messageResult = messageRepository.deleteMessage(message, messageDeleteRequestDto.getChannelId());

//        바이너리 삭제 로직
        boolean binaryContentResult = binaryContentRepository.findAllAttachmentsByOwnerId(messageDeleteRequestDto.getMessageId())
                .stream()
                .allMatch(file -> binaryContentRepository.deleteBinaryContentById(file.getId()));

        return messageResult && binaryContentResult;
    }
}
