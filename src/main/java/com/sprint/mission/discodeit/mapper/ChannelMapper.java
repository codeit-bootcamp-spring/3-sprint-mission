package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {
    UUID channelId = channel.getId();

    // 참여자 정보
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
    List<UserDto> participants = readStatuses.stream()
        .map(ReadStatus::getUser)
        .distinct()
        .map(userMapper::toDto)
        .toList();

    // 최근 메세지의 생성 시각
    List<Message> messages = messageRepository.findAllByChannelId(channelId);
    Instant lastMessageAt = messages.stream()
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(null);

    return new ChannelDto(
        channelId,
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );

  }

}
