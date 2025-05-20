package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Optional;
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
  public ReadStatus create(ReadStatusCreateRequest request) {
    userRepository.findById(request.userId())
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(request.userId(), request.channelId()));

    channelRepository.findById(request.channelId())
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(request.userId(), request.channelId()));

    readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .ifPresent(existingStatus -> {
          throw ReadStatusException.duplicate(request.userId(), request.channelId());
        });

    ReadStatus readStatus = ReadStatus.create(request);

    return readStatusRepository.save(readStatus);
  }


  @Override
  public Optional<ReadStatus> find(UUID id) {
    return Optional.ofNullable(readStatusRepository.findById(id)
        .orElseThrow(() -> ReadStatusException.notFound(id)));
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  public ReadStatus update(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> ReadStatusException.notFound(id));

    readStatus.updateLastReadAt();
    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID id) {
    if (readStatusRepository.findById(id).isEmpty()) {
      throw ReadStatusException.notFound(id);
    }
    readStatusRepository.delete(id);
  }
}
