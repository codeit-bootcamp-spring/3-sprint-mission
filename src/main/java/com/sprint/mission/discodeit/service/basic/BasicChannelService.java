package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.MapperFacade;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final MapperFacade mapperFacade;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 요청 - 이름: {}, 설명: {}", request.name(), request.description());

    Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    Channel savedChannel = channelRepository.save(channel);

    log.info("공개 채널 생성 완료 - 채널 ID: {}", savedChannel.getId());
    return savedChannel;
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청 - 참가자 ID: {}", request.participantIds());

    validateParticipants(request.participantIds());

    // 1. 비공개 채널 생성
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel savedChannel = channelRepository.save(channel);
    log.info("비공개 채널 생성 완료 - 채널 ID: {}", savedChannel.getId());

    // 2. 참가자 조회 및 검증
    List<User> participants = validateAndGetParticipants(request.participantIds(), savedChannel);

    // 3. ReadStatus 생성
    createReadStatuses(participants, savedChannel);

    log.info("비공개 채널 생성 최종 완료 - 채널 ID: {}, 참가자 수: {}",
        savedChannel.getId(), participants.size());
    return savedChannel;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    // N+1 문제 해결: 참가자 정보를 Fetch Join으로 한 번에 조회
    return channelRepository.findByIdWithParticipants(channelId)
        .map(mapperFacade::toDto)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.info("사용자 채널 목록 조회 요청 - 사용자 ID: {}", userId);

    validateUserExists(userId);

    Set<UUID> subscribedChannelIds = getSubscribedChannelIds(userId);
    List<Channel> accessibleChannels = getAccessibleChannels(subscribedChannelIds);

    log.info("접근 가능한 채널 수: {}, 구독 비공개 채널 수: {}",
        accessibleChannels.size(), subscribedChannelIds.size());

    // N+1 문제 해결: 모든 채널의 마지막 메시지 시간을 한 번에 조회
    List<UUID> channelIds = accessibleChannels.stream()
        .map(Channel::getId)
        .collect(Collectors.toList());

    Map<UUID, Instant> lastMessageTimes = getLastMessageTimesForChannels(channelIds);

    return accessibleChannels.stream()
        .map(channel -> mapToChannelDtoWithLastMessageTime(channel, lastMessageTimes))
        .collect(Collectors.toList());
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("채널 수정 요청 - 채널 ID: {}", channelId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));

    if (channel.getType().isPrivate()) {
      throw PrivateChannelUpdateException.of();
    }

    channel.update(request.newName(), request.newDescription());
    log.info("채널 수정 완료 - 채널 ID: {}", channelId);

    return channel;
  }

  @Override
  public void delete(UUID channelId) {
    log.info("채널 삭제 요청 - 채널 ID: {}", channelId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));

    channelRepository.delete(channel);
    log.info("채널 삭제 완료 - 채널 ID: {}", channelId);
  }

  // === Private Helper Methods ===

  /**
   * 참가자 목록의 유효성을 검증합니다.
   * 중복된 참가자가 있는지 확인합니다.
   * 
   * @param participantIds 검증할 참가자 ID 목록
   * @throws IllegalArgumentException 중복된 참가자가 있는 경우
   */
  private void validateParticipants(List<UUID> participantIds) {
    Set<UUID> uniqueParticipantIds = new HashSet<>(participantIds);
    if (uniqueParticipantIds.size() != participantIds.size()) {
      throw new IllegalArgumentException("중복된 참가자가 있습니다.");
    }
  }

  /**
   * 참가자들을 조회하고 유효성을 검증합니다.
   * 요청된 모든 참가자가 존재하는지 확인하고, 실패 시 생성된 채널을 삭제합니다.
   * 
   * @param participantIds 참가자 ID 목록
   * @param savedChannel   생성된 채널 (실패 시 삭제용)
   * @return 조회된 참가자 목록
   * @throws UserNotFoundException 일부 사용자를 찾을 수 없는 경우
   */
  private List<User> validateAndGetParticipants(List<UUID> participantIds, Channel savedChannel) {
    List<User> participants = userRepository.findAllById(participantIds);
    log.info("참가자 조회 완료 - 요청된 수: {}, 실제 조회된 수: {}",
        participantIds.size(), participants.size());

    if (participants.size() != participantIds.size()) {
      channelRepository.delete(savedChannel);
      throw UserNotFoundException.of();
    }

    return participants;
  }

  /**
   * 참가자들의 ReadStatus를 생성합니다.
   * 각 참가자가 채널에 참여할 수 있도록 ReadStatus를 생성하고 저장합니다.
   * 
   * @param participants 참가자 목록
   * @param channel      채널
   */
  private void createReadStatuses(List<User> participants, Channel channel) {
    List<ReadStatus> readStatuses = participants.stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .collect(Collectors.toList());

    readStatusRepository.saveAll(readStatuses);
    log.info("ReadStatus 생성 완료 - 개수: {}", readStatuses.size());
  }

  /**
   * 사용자 존재 여부를 확인합니다.
   * 
   * @param userId 확인할 사용자 ID
   * @throws UserNotFoundException 사용자가 존재하지 않는 경우
   */
  private void validateUserExists(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withUserId(userId);
    }
  }

  /**
   * 사용자가 구독한 채널 ID 목록을 조회합니다.
   * 
   * @param userId 사용자 ID
   * @return 구독한 채널 ID 집합
   */
  private Set<UUID> getSubscribedChannelIds(UUID userId) {
    // N+1 문제 해결: 채널 ID만 효율적으로 조회 (엔티티 로딩 없이 ID만 조회)
    return new HashSet<>(readStatusRepository.findChannelIdsByUserId(userId));
  }

  /**
   * 사용자가 접근 가능한 채널 목록을 조회합니다.
   * 공개 채널과 구독한 비공개 채널이 포함됩니다.
   * 
   * 성능 최적화: DB 레벨에서 필터링하여 메모리 사용량을 대폭 절약
   * 
   * @param subscribedChannelIds 구독한 채널 ID 집합
   * @return 접근 가능한 채널 목록
   */
  private List<Channel> getAccessibleChannels(Set<UUID> subscribedChannelIds) {
    if (subscribedChannelIds.isEmpty()) {
      // 구독한 비공개 채널이 없는 경우 공개 채널만 조회
      log.debug("구독한 비공개 채널이 없음, 공개 채널만 조회");
      return channelRepository.findPublicChannelsWithParticipants();
    } else {
      // 공개 채널 + 구독한 비공개 채널을 DB에서 필터링하여 조회
      List<UUID> subscribedChannelIdsList = new ArrayList<>(subscribedChannelIds);
      log.debug("DB에서 필터링 조회 - 구독한 비공개 채널 수: {}", subscribedChannelIdsList.size());
      return channelRepository.findAccessibleChannelsWithParticipants(subscribedChannelIdsList);
    }
  }

  /**
   * 채널을 ChannelDto로 매핑합니다.
   * 비공개 채널의 경우 참가자 정보를 포함하여 매핑합니다.
   * 
   * @param channel 매핑할 채널
   * @return 매핑된 ChannelDto
   */
  private ChannelDto mapToChannelDto(Channel channel) {
    if (channel.getType().isPrivate()) {
      return mapPrivateChannelToDto(channel);
    } else {
      return mapperFacade.toDto(channel);
    }
  }

  /**
   * 비공개 채널을 ChannelDto로 매핑합니다.
   * 참가자 정보와 마지막 메시지 시간을 포함합니다.
   * 
   * @param channel 매핑할 비공개 채널
   * @return 매핑된 ChannelDto
   */
  private ChannelDto mapPrivateChannelToDto(Channel channel) {
    // N+1 문제 해결: 사용자 정보를 Fetch Join으로 한 번에 조회
    List<ReadStatus> channelReadStatuses = readStatusRepository.findAllByChannelIdWithUser(channel.getId());
    List<UserDto> participants = channelReadStatuses.stream()
        .map(readStatus -> mapperFacade.toDto(readStatus.getUser()))
        .collect(Collectors.toList());

    Instant lastMessageAt = getLastMessageTime(channel);

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt);
  }

  /**
   * 채널의 마지막 메시지 시간을 조회합니다.
   * 메시지가 없는 경우 채널 생성 시간을 반환합니다.
   * 
   * @param channel 확인할 채널
   * @return 마지막 메시지 시간 또는 채널 생성 시간
   */
  private Instant getLastMessageTime(Channel channel) {
    try {
      // N+1 문제 해결: 효율적인 쿼리로 마지막 메시지 시간만 조회
      return messageRepository.findLastMessageTimeByChannelId(channel.getId())
          .orElse(channel.getCreatedAt());
    } catch (Exception e) {
      log.warn("메시지 조회 실패, 채널 생성 시간 사용 - 채널 ID: {}, 오류: {}",
          channel.getId(), e.getMessage());
      return channel.getCreatedAt();
    }
  }

  /**
   * 여러 채널의 마지막 메시지 시간을 한 번에 조회합니다.
   * N+1 문제를 해결하기 위해 배치 쿼리를 사용합니다.
   * 
   * @param channelIds 조회할 채널 ID 목록
   * @return 채널 ID별 마지막 메시지 시간 맵
   */
  private Map<UUID, Instant> getLastMessageTimesForChannels(List<UUID> channelIds) {
    if (channelIds.isEmpty()) {
      return Collections.emptyMap();
    }

    try {
      List<Object[]> results = messageRepository.findLastMessageTimesByChannelIds(channelIds);
      return results.stream()
          .collect(Collectors.toMap(
              result -> (UUID) result[0],
              result -> (Instant) result[1]));
    } catch (Exception e) {
      log.warn("마지막 메시지 시간 배치 조회 실패 - 채널 수: {}, 오류: {}", channelIds.size(), e.getMessage());
      return Collections.emptyMap();
    }
  }

  /**
   * 채널을 ChannelDto로 매핑하되, 미리 조회한 마지막 메시지 시간을 사용합니다.
   * N+1 문제를 해결하기 위해 배치로 조회한 데이터를 활용합니다.
   * 
   * @param channel          매핑할 채널
   * @param lastMessageTimes 미리 조회한 마지막 메시지 시간 맵
   * @return 매핑된 ChannelDto
   */
  private ChannelDto mapToChannelDtoWithLastMessageTime(Channel channel, Map<UUID, Instant> lastMessageTimes) {
    if (channel.getType().isPrivate()) {
      return mapPrivateChannelToDtoWithLastMessageTime(channel, lastMessageTimes);
    } else {
      // 공개 채널의 경우
      Instant lastMessageAt = lastMessageTimes.getOrDefault(channel.getId(), channel.getCreatedAt());
      return new ChannelDto(
          channel.getId(),
          channel.getType(),
          channel.getName(),
          channel.getDescription(),
          Collections.emptyList(), // 공개 채널은 참가자 목록이 없음
          lastMessageAt);
    }
  }

  /**
   * 비공개 채널을 ChannelDto로 매핑하되, 미리 조회한 마지막 메시지 시간을 사용합니다.
   * 
   * @param channel          매핑할 비공개 채널
   * @param lastMessageTimes 미리 조회한 마지막 메시지 시간 맵
   * @return 매핑된 ChannelDto
   */
  private ChannelDto mapPrivateChannelToDtoWithLastMessageTime(Channel channel, Map<UUID, Instant> lastMessageTimes) {
    // 이미 Fetch Join으로 로딩된 ReadStatus와 User 정보 사용
    List<UserDto> participants = channel.getReadStatuses().stream()
        .map(readStatus -> mapperFacade.toDto(readStatus.getUser()))
        .collect(Collectors.toList());

    Instant lastMessageAt = lastMessageTimes.getOrDefault(channel.getId(), channel.getCreatedAt());

    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participants,
        lastMessageAt);
  }
}
