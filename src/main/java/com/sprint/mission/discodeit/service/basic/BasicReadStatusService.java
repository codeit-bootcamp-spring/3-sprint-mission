package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
    UUID userId = readStatusCreateRequest.getUserId();
    UUID channelId = readStatusCreateRequest.getChannelId();
    Instant lastReadAt = readStatusCreateRequest.getLastReadAt();

    // 의존관계 유효성( 존재하지 않는다면 예외 발생 )
    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }
    if (readStatusRepository.findAllByUserId(userId).stream()
        .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
      throw new IllegalArgumentException(
          "ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
    }

    // Create
    ReadStatus readStatus = new ReadStatus(
        userId,
        channelId,
        lastReadAt
    );
    return readStatusRepository.save(readStatus);
  }

  @Override
  public ReadStatus find(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
  }


  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .toList();
  }

  @Override
  public ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest) {

    Instant newLastReadAt = readStatusUpdateRequest.getNewLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + id + " not found"));

    // Update
    readStatus.update(newLastReadAt);
    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID id) {
    // 유효성
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("ReadStatus with id " + id + " not found");
    }
    readStatusRepository.deleteById(id);
  }
}
