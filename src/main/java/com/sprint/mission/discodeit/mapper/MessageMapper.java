package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final UserMapper userMapper;

    public MessageDto toDto(Message entity) {
        if (entity == null) {
            return null;
        }

        List<BinaryContent> attachments = entity.getAttachments();
        return MessageDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .content(entity.getContent())
                .channelId(entity.getChannel().getId())
                .author(userMapper.toDto(entity.getAuthor()))
                .attachments(attachments.stream()
                        .map(binaryContentMapper::toDto)
                        .toList())
                .build();
    }
}