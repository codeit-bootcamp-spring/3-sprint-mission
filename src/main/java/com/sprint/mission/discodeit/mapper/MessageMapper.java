package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  public MessageDto toDto(Message entity) {
    return new MessageDto(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getContent(),
        entity.getChannel().getId(),
        userMapper.toDto(entity.getAuthor()),
        entity.getAttachments().stream()
            .map(binaryContentMapper::toDto)
            .collect(Collectors.toList())
    );
  }
}
