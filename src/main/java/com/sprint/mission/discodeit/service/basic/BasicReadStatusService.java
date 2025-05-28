package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(UUID userId, UUID channelId) {
    userRepository.findById(userId)
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(userId, channelId));

    channelRepository.findById(channelId)
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(userId, channelId));

    readStatusRepository.findByUserIdAndChannelId(userId, channelId)
        .ifPresent(existingStatus -> {
          throw ReadStatusException.duplicate(userId, channelId);
        });

    ReadStatus readStatus = ReadStatus.create(userId, channelId);

    return readStatusRepository.save(readStatus);
  }


  @Override
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> ReadStatusException.notFound(readStatusId));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  public ReadStatus update(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> ReadStatusException.notFound(readStatusId));

    readStatus.updateLastReadAt();
    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID reaStatusId) {
    if (readStatusRepository.findById(reaStatusId).isEmpty()) {
      throw ReadStatusException.notFound(reaStatusId);
    }
    readStatusRepository.delete(reaStatusId);
  }
}
