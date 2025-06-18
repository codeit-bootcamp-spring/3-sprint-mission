package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageResponseDto toDto(Message message) {
        UserResponseDto authorDto = userMapper.toDto(message.getAuthor());
        List<BinaryContentResponseDto> attachments = getAttachments(message);

        return new MessageResponseDto(message.getId(), message.getCreatedAt(), message.getUpdatedAt(),
                message.getContent(), message.getChannel().getId(), authorDto, attachments);
    }

    private List<BinaryContentResponseDto> getAttachments(Message message) {
        List<BinaryContentResponseDto> attachments = new ArrayList<>();

        message.getAttachments().stream()
                .map(binaryContentMapper::toDto)
                .forEach(attachments::add);

        return attachments;
    }
}
