package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
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
        
        // 프록시 객체 안전 접근
        UUID channelId = null;
        try {
            channelId = message.getChannel().getId();
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }
        
        UserDto author = null;
        try {
            author = userMapper.toDto(message.getAuthor());
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 null 처리
        }
        
        List<BinaryContentDto> attachments = null;
        try {
            attachments = message.getAttachments().stream()
                .map(binaryContentMapper::toDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            // 프록시 객체 접근 실패 시 빈 리스트 처리
            attachments = List.of();
        }

        return new MessageDto(
                id,
                createdAt,
                updatedAt,
                content,
                channelId,
                author,
                attachments);
    }
}
