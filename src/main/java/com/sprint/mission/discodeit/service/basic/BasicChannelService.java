package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.mapper.EntityDtoMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.exception.CustomException;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final EntityDtoMapper entityDtoMapper;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelRepository.save(channel);
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel savedChannel = channelRepository.save(channel);

    // User 엔티티들을 로드하여 ReadStatus 생성
    List<User> participants = userRepository.findAllById(request.participantIds());

    List<ReadStatus> readStatuses = participants.stream()
        .map(user -> new ReadStatus(user, savedChannel, savedChannel.getCreatedAt()))
        .collect(Collectors.toList());

    // ReadStatus들을 Channel에 설정 (cascade로 자동 저장됨)
    // 실제로는 cascade 설정에 따라 자동 처리되지만, 여기서는 명시적으로 저장
    savedChannel.getReadStatuses().addAll(readStatuses);

    return savedChannel;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(entityDtoMapper::toDto)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // User의 ReadStatus를 통해 구독 채널 ID 조회 (지연 로딩 활용)
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException.UserNotFoundException("User with id " + userId + " not found"));

    Set<UUID> subscribedChannelIds = user.getReadStatuses().stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .collect(Collectors.toSet());

    return channelRepository.findAll().stream()
        .filter(channel -> channel.getType().equals(ChannelType.PUBLIC)
            || subscribedChannelIds.contains(channel.getId()))
        .map(entityDtoMapper::toDto)
        .toList();
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " not found"));

    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new CustomException.PrivateChannelUpdateException("Private channel cannot be updated");
    }

    // 변경 감지(Dirty Checking) 활용 - save() 호출 불필요
    channel.update(newName, newDescription);

    return channel; // 트랜잭션 커밋 시 자동으로 변경 사항 반영
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new CustomException.ChannelNotFoundException("Channel with id " + channelId + " not found"));

    // cascade = ALL, orphanRemoval = true로 설정되어 있어
    // Channel 삭제 시 연관된 Message, ReadStatus 자동 삭제됨
    channelRepository.delete(channel);
  }
}
