package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message msg) {
        if (msg == null) return null;
        return new MessageDto(
            msg.getId(),
            msg.getCreatedAt(),
            msg.getUpdatedAt(),
            msg.getContent(),
            msg.getChannel().getId(),
            userMapper.toDto(msg.getAuthor()),
            msg.getAttachments().stream()
                .map(binaryContentMapper::toDto)
                .collect(Collectors.toList())
        );
    }
}