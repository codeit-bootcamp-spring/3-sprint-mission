package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryContentType;
import com.sprint.mission.discodeit.entity.dto.binaryContent.BinaryOwnerType;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    public JCFMessageService(BinaryContentRepository binaryContentRepository, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository, UserStatusRepository userStatusRepository) {
        this.binaryContentRepository = binaryContentRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDto) {
        Message message = messageRepository.createMessage(messageCreateRequestDto);
        MessageResponseDto messageResponseDto;
        if (!messageCreateRequestDto.getMessageFile().isEmpty()) {
//            바이너리 생성 로직
            messageCreateRequestDto.getMessageFile().forEach(file -> {
                BinaryContentCreateRequestDto binaryContentCreateRequestDto = new BinaryContentCreateRequestDto(message.getId(), BinaryContentType.MESSAGE_ATTACHMENT, BinaryOwnerType.MESSAGE, file);
                binaryContentRepository.createBinaryContent(binaryContentCreateRequestDto);
            });

            messageResponseDto = new MessageResponseDto(message);
        } else {
            messageResponseDto = new MessageResponseDto(message);
        }
        // user 온라인 상태 변경
        userStatusRepository.updateByUserId(messageCreateRequestDto.getUserId()) ;
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

//        바이너리 파일 삭제 로직
        binaryContentRepository.findAllBinaryContentById(messageDeleteRequestDto.getMessageId()).forEach(file -> {
            binaryContentRepository.deleteBinaryContentById(messageDeleteRequestDto.getMessageId());
        });
    }

}
