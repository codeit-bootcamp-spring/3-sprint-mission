package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JcfChannelService implements ChannelService {
  private final List<Channel> channels = new ArrayList<>();

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  public JcfChannelService(UserRepository userRepository, MessageRepository messageRepository ) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
  }


  @Override
  public Channel create(PublicChannelCreateRequest request) {
    // 중복 채널 확인
    boolean isDuplicate = channels.stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(request.ownerUserId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다. 다른 이름을 설정해주세요.");
    }

    User ownerUser = userRepository.findById(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

    List<User> members = new ArrayList<>();
    members.add(ownerUser);

    Channel channel = new Channel(request.channelName(), ownerUser, members, ChannelType.PUBLIC);
    channels.add(channel);
    return channel;
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    // 중복 채널 확인
    boolean isDuplicate = channels.stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(request.ownerUserId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다. 다른 이름을 설정해주세요.");
    }

    User ownerUser = userRepository.findById(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

    List<User> members = new ArrayList<>();
    members.add(ownerUser);
    // Private 채널의 추가 멤버들
    for (UUID memberId : request.memberIds()) {
      User member = userRepository.findById(memberId)
          .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
      members.add(member);
    }

    Channel channel = new Channel(request.channelName(), ownerUser, members, ChannelType.PRIVATE);
    channels.add(channel);
    return channel;
  }

  @Override
  public Optional<ChannelDto> find(UUID channelId) {
    return channels.stream()
        .filter(channel -> channel.getId().equals(channelId))
        .map(channel -> {
          List<Message> messages = messageRepository.findByChannelId(channelId);

          LocalDateTime lastMessageTime = messages.stream()
              .map(Message::getCreatedAt)
              .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
              .max(LocalDateTime::compareTo)
              .orElse(null);

          List<UUID> memberIds = (channel.getChannelType() == ChannelType.PRIVATE) ?
              channel.getChannelMembers().stream()
                  .map(User::getId)
                  .collect(Collectors.toList()) : null;

          return new ChannelDto(
              channel.getId(),
              channel.getChannelName(),
              channel.getChannelOwner().getId(),
              channel.getChannelType(),
              memberIds,
              lastMessageTime
          );
        })
        .findFirst();
  }


  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    return channels.stream()
        .filter(channel -> channel.getChannelOwner().getId().equals(userId)) // 사용자가 만든 채널 필터링
        .map(channel -> {
          List<Message> messages = messageRepository.findByChannelId(channel.getId());

          LocalDateTime lastMessageTime = messages.stream()
              .map(Message::getCreatedAt)
              .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
              .max(LocalDateTime::compareTo)
              .orElse(null);

          List<UUID> memberIds = (channel.getChannelType() == ChannelType.PRIVATE) ?
              channel.getChannelMembers().stream()
                  .map(User::getId)
                  .collect(Collectors.toList()) : null;

          return new ChannelDto(
              channel.getId(),
              channel.getChannelName(),
              channel.getChannelOwner().getId(),
              channel.getChannelType(),
              memberIds,
              lastMessageTime
          );
        })
        .collect(Collectors.toList());
  }


  private Channel getChannelById(UUID channelId) {
    return channels.stream()
        .filter(c -> c.getId().equals(channelId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = getChannelById(channelId);

    // 새로운 채널 이름이 기존 채널 이름과 동일한지 확인
    if (channel.getChannelName().equals(request.channelName())) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다. 다른 이름을 입력해주세요.");
    }

    // 채널 소유자가 동일한 이름을 가진 채널을 다른 채널에서 사용하는지 확인
    UUID ownerId = channel.getChannelOwner().getId();
    boolean isDuplicate = channels.stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("해당 채널명은 이미 사용 중입니다.");
    }

    // 채널 이름 수정
    channel.updateChannelName(request.channelName());

    return channel;
  }


  @Override
  public void deleteChannel(UUID channelId) {
    //channels.remove(getChannelById(channelId));
    channels.removeIf(e -> e.getId().equals(channelId));
  }


  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.addChannelUser(user);
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = getChannelById(channelId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.removeChannelUser(user);
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = getChannelById(channelId);
    return new ArrayList<>(channel.getChannelMembers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channels.removeIf(channel -> channel.getChannelOwner().getId().equals(userId));
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channels) {
      channel.getChannelMembers().removeIf(user -> user.getId().equals(userId));
    }
  }

  /*   [refactoring]
  서비스 계층에서 유저나 채널이 null일 수 있는 경우, Optional<T>를 사용하는 편인가요?
  CRUD 모든 메서드에 null 체크를 넣는 건 코드가 장황해질 수 있는데, 보통은 어떻게 처리하나요?

  private Optional<Channel> findChannelById(UUID channelId) {
    return channels.stream()
        .filter(c -> c.getId().equals(channelId))
        .findFirst();
  }

 @Override
  public Channel getChannelById(UUID channelId) {
    return findChannelById(channelId).orElse(null);
  }

  ===================================================================================
  커스텀 예외처리 updateUserName() 에서 null체크 생략 가능
@Override
public User getUserById(UUID id) {
  return data.stream()
      .filter(e -> e.getId().equals(id))
      .findFirst()
      .orElseThrow(() -> new UserNotFoundException(id)); //
}

public void updateUserName(UUID id, String name) {
  User user = getUserById(id); // 못 찾으면 여기서 예외 던짐
//  if (user == null) {
//    throw new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id);
//  }
  user.updateName(name);
}

   */
}