package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelAssembler {

  private final UserMapper userMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserStatusRepository userStatusRepository;

  public Channel toEntity(PublicChannelCreateRequest request) {
    return Channel.createPublic(request.name(), request.description());
  }

  public Channel toEntity(PrivateChannelCreateRequest request) {
    return Channel.createPrivate();
  }

  public void updateEntity(PublicChannelUpdateRequest request, Channel channel) {
    if (request.newName() != null) {
      channel.updateName(request.newName());
    }
    if (request.newDescription() != null) {
      channel.updateDescription(request.newDescription());
    }
  }

  public ChannelResponse toResponse(Channel channel) {
    Instant lastMessageAt = messageRepository.findLastCreatedAtByChannelId(channel.getId());

    List<UserResponse> participants = List.of();
    if (channel.getType() == ChannelType.PRIVATE) {
      participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
          .map(ReadStatus::getUser)
          .map(this::toUserResponseWithStatus)
          .collect(Collectors.toList());
    }

    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt
    );
  }

  private UserResponse toUserResponseWithStatus(User user) {
    boolean isOnline = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline)
        .orElse(false);

    UserResponse base = userMapper.toResponse(user);
    return new UserResponse(
        base.id(),
        base.username(),
        base.email(),
        base.profile(),
        isOnline
    );
  }
}
