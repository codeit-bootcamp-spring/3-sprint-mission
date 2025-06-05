package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusResponse create(UUID userId, UUID channelId) {
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(userId, channelId));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(userId, channelId));

    readStatusRepository.findByUserIdAndChannelId(userId, channelId)
        .ifPresent(existingStatus -> {
          throw ReadStatusException.duplicate(userId, channelId);
        });

    ReadStatus readStatus = ReadStatus.create(user, channel);

    return ReadStatusResponse.from(readStatusRepository.save(readStatus));
  }


  @Override
  public ReadStatusResponse find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(ReadStatusResponse::from)
        .orElseThrow(() -> ReadStatusException.notFound(readStatusId));
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatusResponse::from).toList();
  }

  @Override
  public ReadStatusResponse update(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> ReadStatusException.notFound(readStatusId));

    readStatus.updateLastReadAt();
    return ReadStatusResponse.from(readStatusRepository.save(readStatus));
  }

  @Override
  public void delete(UUID reaStatusId) {
    if (readStatusRepository.findById(reaStatusId).isEmpty()) {
      throw ReadStatusException.notFound(reaStatusId);
    }
    readStatusRepository.deleteById(reaStatusId);
  }
}
