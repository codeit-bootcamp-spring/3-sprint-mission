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
import java.util.Map;
import java.util.UUID;
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
      // 참여자 조회
      List<User> users = readStatusRepository.findAllByChannelId(channel.getId()).stream()
          .map(ReadStatus::getUser)
          .toList();

      // userId 리스트 추출
      List<UUID> userIds = users.stream()
          .map(User::getId)
          .toList();

      // 온라인 상태 일괄 조회 및 Map 캐싱
      Map<UUID, UserStatus> userStatusMap = userStatusRepository.findByUserIdIn(userIds).stream()
          .collect(Collectors.toMap(us -> us.getUser().getId(), us -> us));

      participants = users.stream()
          .map(user -> toUserResponseWithStatus(user, userStatusMap.get(user.getId())))
          .toList();
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

  private UserResponse toUserResponseWithStatus(User user, UserStatus userStatus) {
    boolean isOnline = userStatus != null && userStatus.isOnline();

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
