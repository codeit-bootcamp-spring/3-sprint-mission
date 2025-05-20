package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

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

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);
    return readStatusRepository.save(readStatus);
  }


  @Override
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .toList();
  }


  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    System.out.println("🛠️ Service.update() called");
    System.out.println(">> readStatusId: " + readStatusId);
    System.out.println(">> newLastReadAt: " + newLastReadAt);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          System.out.println("❌ ReadStatus not found with id: " + readStatusId);
          return new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        });

    System.out.println("📦 Before update: " + readStatus);

    readStatus.update(newLastReadAt);

    System.out.println("✅ After update: " + readStatus);

    ReadStatus saved = readStatusRepository.save(readStatus);
    System.out.println("💾 Saved: " + saved);

    return saved;
  }


  @Override
  public void delete(UUID readStatusId) {
    readStatusRepository.deleteById(readStatusId);
  }
}
