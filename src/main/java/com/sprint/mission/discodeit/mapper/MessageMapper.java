package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.serviceDto.MessageDto;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class MessageMapper {

    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message message) {
        UUID id = message.getId();
        Instant createdAt = message.getCreatedAt();
        Instant updatedAt = message.getUpdatedAt();
        String content = message.getContent();
        UUID channelId = message.getChannel().getId();
        UserDto author = userMapper.toDto(message.getAuthor());
        List<BinaryContentDto> attachments = message.getAttachments().stream()
            .map(bc -> {
                try {
                    return binaryContentMapper.toDto(bc);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());

        return new MessageDto(id, createdAt, updatedAt, content, channelId, author, attachments);
    }
}
