package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;

  public MessageResponse entityToDto(Message message) {
    return MessageResponse.builder()
        .id(message.getId())
        .author(message.getAuthor() == null ? UserResponse.builder()
            .id(null)
            .username("탈퇴한 사용자")
            .email("")
            .profile(null)
            .online(false)
            .build()
            : userMapper.entityToDto(message.getAuthor()))
        .channelId(message.getChannel().getId())
        .content(message.getContent() == null ? "" : message.getContent())
        .createdAt(message.getCreatedAt())
        .updatedAt(message.getUpdatedAt())
        .attachments(message.getAttachments() == null ? List.of() : getAttachments(message))
        .build();
  }

  public List<BinaryContentResponse> getAttachments(Message message) {
    return message.getAttachments().stream()
        .map(binaryContentMapper::entityToDto)
        .collect(Collectors.toList());
  }


}
