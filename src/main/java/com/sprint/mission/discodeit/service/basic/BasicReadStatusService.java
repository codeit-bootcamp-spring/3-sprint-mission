package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();
    Instant lastReadAt = request.lastReadAt();

    // 의존관계 유효성( 존재하지 않는다면 예외 발생 )
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    if (readStatusRepository.findAllByUserId(userId).stream()
        .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId))) {
      throw new IllegalArgumentException(
          "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
    }

    // Create
    ReadStatus readStatus = new ReadStatus(
        user,
        channel,
        lastReadAt
    );
    return readStatusRepository.save(readStatus);
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
  }


  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .toList();
  }

  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {

    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));

    // Update
    readStatus.update(newLastReadAt);
    return readStatus;
  }

  @Override
  public void delete(UUID readStatusId) {
    // 유효성
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
