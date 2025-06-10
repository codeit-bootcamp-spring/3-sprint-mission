package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel entity) {
    UUID channelId = entity.getId();

    List<UserDto> participants = entity.getType() == ChannelType.PRIVATE
        ? readStatusRepository.findAllByChannel_Id(channelId).stream()
        .map(ReadStatus::getUser)
        .map(userMapper::toDto)
        .collect(Collectors.toList())
        : List.of();
    Instant lastMessageAt = messageRepository.findLastMessageTimeByChannelId(channelId)
        .orElse(Instant.MIN);

    return new ChannelDto(
        entity.getId(),
        entity.getType(),
        entity.getName(),
        entity.getDescription(),
        participants,
        lastMessageAt
    );
  }

  public ChannelResponse toResponse(Channel entity) {
    UUID channelId = entity.getId();

    Instant lastMessageAt = messageRepository.findLastMessageTimeByChannelId(channelId)
        .orElse(Instant.MIN);

    List<UUID> participantIds = entity.getType() == ChannelType.PRIVATE
        ? readStatusRepository.findAllByChannel_Id(channelId).stream()
        .map(rs -> rs.getUser().getId())
        .toList()
        : List.of();

    return new ChannelResponse(
        entity.getId(),
        entity.getType(),
        entity.getName(),
        entity.getDescription(),
        lastMessageAt,
        participantIds
    );
  }

}
