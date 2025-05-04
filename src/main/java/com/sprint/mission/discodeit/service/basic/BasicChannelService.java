package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    User ownerUser = userRepository.findById(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("유효한 사용자 ID가 아닙니다."));

    boolean isDuplicate = channelRepository.findAll().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerUser.getId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
    }

    List<User> members = new ArrayList<>();
    members.add(ownerUser);

    Channel channel = new Channel(request.channelName(), ownerUser, members, ChannelType.PUBLIC);
    return channelRepository.save(channel);
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    User ownerUser = userRepository.findById(request.ownerUserId())
        .orElseThrow(() -> new IllegalArgumentException("유효한 사용자 ID가 아닙니다."));

    // 채널 이름 중복 확인
    boolean isDuplicate = channelRepository.findAll().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerUser.getId())
            && c.getChannelName().equals(request.channelName()));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 같은 이름의 채널이 존재합니다.");
    }

    // 채널 생성 시 참여자와 ReadStatus 처리
    List<User> members = new ArrayList<>();
    members.add(ownerUser);

    Channel channel = new Channel(request.channelName(), ownerUser, members, ChannelType.PRIVATE);
    // ReadStatus 생성 (참여자들에 대해 ReadStatus 설정)
    for (UUID userId : request.memberIds()) {
      User member = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("유효한 사용자 ID가 아닙니다."));
      members.add(member);
      // ReadStatus 생성 로직 추가
      ReadStatus readStatus = new ReadStatus(member.getId(), channel.getId());
      readStatusRepository.save(readStatus);
    }

    return channelRepository.save(channel);
  }


  @Override
  public Optional<ChannelDto> find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

    // 가장 최근 메시지 시간 조회
    List<Message> messages = messageRepository.findByChannelId(channelId);

    LocalDateTime lastMessageTime = messages.stream()
        .map(Message::getCreatedAt)
        .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
        .max(LocalDateTime::compareTo)
        .orElse(null);

    // PRIVATE 채널이면 멤버 ID 리스트 포함
    List<UUID> memberIds = null;
    if (channel.getChannelType() == ChannelType.PRIVATE) {
      memberIds = channel.getChannelMembers().stream()
          .map(User::getId)
          .collect(Collectors.toList());
    }

    ChannelDto channelDto = new ChannelDto(
        channel.getId(),
        channel.getChannelName(),
        channel.getChannelOwner().getId(),
        channel.getChannelType(),
        memberIds,
        lastMessageTime
    );

    return Optional.of(channelDto);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // 1. PUBLIC 채널은 전체 조회
    // 2. PRIVATE 채널은 userId가 참여한 채널만 조회
    List<Channel> channels = channelRepository.findAll();

    return channels.stream()
        .filter(channel -> {
          if (channel.getChannelType() == ChannelType.PUBLIC) {
            return true; // PUBLIC 채널은 모두 조회
          } else if (channel.getChannelType() == ChannelType.PRIVATE) {
            // PRIVATE 채널인 경우 userId가 참여한 채널만 조회
            return channel.getChannelMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));
          }
          return false;
        })
        .map(channel -> {
          List<Message> messages = messageRepository.findByChannelId(channel.getId());
          LocalDateTime lastMessageTime = messages.stream()
              .map(Message::getCreatedAt)
              .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
              .max(LocalDateTime::compareTo)
              .orElse(null);

          List<UUID> memberIds = null;
          if (channel.getChannelType() == ChannelType.PRIVATE) {
            memberIds = channel.getChannelMembers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
          }

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

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request){
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

    String newChannelName = request.channelName();

    if (channel.getChannelName().equals(newChannelName)) {
      throw new IllegalArgumentException("채널 이름이 기존과 동일합니다.");
    }

    UUID ownerId = channel.getChannelOwner().getId();
    boolean isDuplicate = channelRepository.findAll().stream()
        .anyMatch(c -> c.getChannelOwner().getId().equals(ownerId)
            && c.getChannelName().equals(newChannelName));

    if (isDuplicate) {
      throw new IllegalArgumentException("이미 해당 채널명이 존재합니다.");
    }

    channel.updateChannelName(newChannelName);
    channelRepository.update(channel);
    return channel;
  }

  @Override
  public void deleteChannel(UUID channelId) {
    // 1. 채널 존재 여부 검증
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

    // 2. 관련된 메시지 삭제
    List<Message> messages = messageRepository.findByChannelId(channelId);
    messageRepository.deleteAll(messages);

    // 3. 관련된 읽음 상태(읽은 메시지) 삭제
    List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channelId);
    readStatusRepository.deleteAll(readStatuses);

    // 4. 채널 삭제
    channelRepository.delete(channelId);
  }


  @Override
  public void addMember(UUID channelId, UUID userId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.addChannelUser(user);
    channelRepository.update(channel);
  }

  @Override
  public void removeMember(UUID channelId, UUID userId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 ID입니다."));

    channel.removeChannelUser(user);
    channelRepository.update(channel);
  }

  @Override
  public List<User> getChannelMembers(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 ID입니다."));
    return new ArrayList<>(channel.getChannelMembers());
  }

  @Override
  public void deleteChannelsCreatedByUser(UUID userId) {
    channelRepository.deleteByOwnerId(userId);
  }

  @Override
  public void removeUserFromAllChannels(UUID userId) {
    channelRepository.removeUserFromAllChannels(userId);
  }
}
