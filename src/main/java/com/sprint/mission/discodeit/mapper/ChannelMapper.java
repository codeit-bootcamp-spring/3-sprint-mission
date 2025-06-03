package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {
    if (channel == null) {
      return null;
    }

    Instant lastMessageAt_DTO = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(
            channel.getId())
        .map(BaseEntity::getCreatedAt)
        .orElse(Instant.MIN);

    List<UserDto> participants_DTO = new ArrayList<>();

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      participants_DTO = readStatusRepository.findUsersByChannelId(channel.getId())
          .stream()
          .map(userMapper::toDto)
          .collect(Collectors.toList());
    }

    return ChannelDto.builder()
        .id(channel.getId())
        .type(channel.getType())
        .name(channel.getName())
        .description(channel.getDescription())
        .participants(participants_DTO)
        .lastMessageAt(lastMessageAt_DTO)
        .build();
  }
}