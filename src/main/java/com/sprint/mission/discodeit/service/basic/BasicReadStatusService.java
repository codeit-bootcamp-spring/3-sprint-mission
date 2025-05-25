package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  public BasicReadStatusService(ReadStatusRepository readStatusRepository,
      UserRepository userRepository, ChannelRepository channelRepository) {
    this.readStatusRepository = readStatusRepository;
    this.userRepository = userRepository;
    this.channelRepository = channelRepository;
  }

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    if (userRepository.findById(request.userId()).isEmpty()) {
      throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
    }

    if (channelRepository.findById(request.channelId()).isEmpty()) {
      throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
    }

    Optional<ReadStatus> existing = readStatusRepository
        .findByUserIdAndChannelId(request.userId(), request.channelId());

    if (existing.isPresent()) {
      throw new IllegalArgumentException("해당 유저와 채널의 ReadStatus는 이미 존재합니다.");
    }

    ReadStatus newStatus = new ReadStatus(request.userId(), request.channelId(), Instant.MIN);

    return readStatusRepository.save(newStatus);
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return readStatusRepository.findById(id);
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  public ReadStatus update(UUID id, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus가 존재하지 않습니다."));

    readStatus.updateLastReadAt(newLastReadAt);
    return readStatusRepository.save(readStatus);
  }


  @Override
  public void delete(UUID id) {
    if (readStatusRepository.findById(id).isEmpty()) {
      throw new IllegalArgumentException("삭제할 ReadStatus가 존재하지 않습니다.");
    }
    readStatusRepository.deleteById(id);
  }
}
