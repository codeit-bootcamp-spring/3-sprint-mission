package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
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

  private final UserMapper userMapper;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  public ChannelResponse entityToDto(Channel channel) {
    return ChannelResponse.builder()
        .id(channel.getId())
        .name(channel.getName() == null ? "이름 없는 채널" : channel.getName())
        .description(channel.getDescription() == null ? " 설명 없음 " : channel.getDescription())
        .type(channel.getType())
        .participants(getParticipants(channel.getId()))
        .lastMessageAt(getLastMessageTime(channel.getId()) == null ? Instant.EPOCH
            : getLastMessageTime(channel.getId()))
        .build();
  }

  private List<UserResponse> getParticipants(UUID channelId) {
    return readStatusRepository.findAllByChannelId(channelId).stream()
        .map(readStatus -> userMapper.entityToDto(readStatus.getUser()))
        .collect(Collectors.toList());
  }

  private Instant getLastMessageTime(UUID channelId) {
    return messageRepository.findTop1ByChannelIdOrderByCreatedAtDesc(channelId)
        .map(BaseEntity::getCreatedAt)
        .orElse(null);
  }

}
