package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelService implements ChannelService {
  private static final String FILE_PATH = "channels.ser";
  private final Map<UUID, Channel> channelData;
  private final UserService userRepository;
  private final MessageRepository messageRepository;

  public FileChannelService(UserService userService, MessageRepository messageRepository) {
    this.userRepository = userService;
    this.messageRepository = messageRepository;
    this.channelData = loadChannelData();
  }

  private void saveChannelData() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(channelData);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<UUID, Channel> loadChannelData() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>();
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Channel>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    UserDto ownerDto = userRepository.find(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    User owner = new User(ownerDto.getUsername(), ownerDto.getEmail());

    boolean isDuplicate = channelData.values().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(owner.getId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
    }

    List<User> members = new ArrayList<>();
    members.add(owner); // 공개 채널은 소유자만 기본 멤버
    Channel channel = new Channel(request.channelName(), owner, members, ChannelType.PUBLIC);
    channelData.put(channel.getId(), channel);
    saveChannelData();
    return channel;
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    UserDto ownerDto = userRepository.find(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    User owner = new User(ownerDto.getUsername(), ownerDto.getEmail());

    boolean isDuplicate = channelData.values().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(owner.getId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
    }

    List<User> members = new ArrayList<>();
    for (UUID memberId : request.memberIds()) {
      UserDto memberDto = userRepository.find(memberId)
          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버 ID: " + memberId));
      members.add(new User(memberDto.getUsername(), memberDto.getEmail()));
    }

    if (members.stream().noneMatch(u -> u.getId().equals(owner.getId()))) {
      members.add(owner);
    }

    Channel channel = new Channel(request.channelName(), owner, members, ChannelType.PRIVATE);
    channelData.put(channel.getId(), channel);
    saveChannelData();
    return channel;
  }

  @Override
  public Optional<ChannelDto> find(UUID channelId) {
    Channel channel = channelData.get(channelId);
    if (channel == null) {
      return Optional.empty();
    }

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

    ChannelDto dto = new ChannelDto(
        channel.getId(),
        channel.getChannelName(),
        channel.getChannelOwner().getId(),
        channel.getChannelType(),
        memberIds,
        lastMessageTime
    );

    return Optional.of(dto);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<ChannelDto> channelsForUser = channelData.values().stream()
        .filter(channel -> channel.getChannelMembers().stream()
            .anyMatch(user -> user.getId().equals(userId)))  // 유저가 속한 채널만 필터링
        .map(channel -> {
          List<UUID> memberIds = (channel.getChannelType() == ChannelType.PRIVATE) ?
              channel.getChannelMembers().stream()
                  .map(User::getId)
                  .collect(Collectors.toList()) : null;

          List<Message> messages = messageRepository.findByChannelId(channel.getId());
          LocalDateTime lastMessageTime = messages.stream()
              .map(Message::getCreatedAt)
              .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
              .max(LocalDateTime::compareTo)
              .orElse(null);

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

    return channelsForUser;
  }


  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    // 해당 채널이 존재하는지 확인
    ChannelDto channelDto = find(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    Channel channel = channelData.get(channelDto.getId());

    // 새 채널 이름과 기존 채널 이름이 동일한지 확인
    if (channel.getChannelName().equals(request.channelName())) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다.");
    }

    // 채널 이름 중복 여부 체크
    UUID ownerId = channel.getChannelOwner().getId();
    boolean isDuplicate = channelData.values().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("해당 채널명은 이미 사용 중입니다.");
    }

    channel.updateChannelName(request.channelName());
    saveChannelData();
    return channel;
  }


  @Override
  public void deleteChannel(UUID channelId) {
    channelData.remove(channelId);
    saveChannelData();
  }

  @Override
  public void addMember(UUID channelId, UUID userId) {
    ChannelDto channelDto = find(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    Channel channel = channelData.get(channelDto.getId());

    UserDto userDTO = userRepository.find(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    // 유저 존재 확인 후 채널에 추가
    User user = new User(userDTO.getUsername(), userDTO.getEmail());
    channel.addChannelUser(user);
    saveChannelData();
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    ChannelDto channelDto = find(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    Channel channel = channelData.get(channelDto.getId());

    UserDto userDTO = userRepository.find(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    // 유저 존재 확인 후 채널에 추가
    User user = new User(userDTO.getUsername(), userDTO.getEmail());
    channel.removeChannelUser(user);
    saveChannelData();
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    ChannelDto channelDto = find(channelId)
        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

    Channel channel = channelData.get(channelDto.getId());

    return new ArrayList<>(channel.getChannelMembers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channelData.entrySet().removeIf(entry -> entry.getValue().getChannelOwner().getId().equals(userId));
    saveChannelData();
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    for (Channel channel : channelData.values()) {
      channel.getChannelMembers().removeIf(user -> user.getId().equals(userId));
    }
    saveChannelData();
  }
}