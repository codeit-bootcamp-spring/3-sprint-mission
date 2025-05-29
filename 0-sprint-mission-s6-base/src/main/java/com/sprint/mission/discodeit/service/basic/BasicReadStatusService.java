package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final ReadStatusMapper readStatusMapper;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusResponse create(ReadStatusRequest.Create request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId).orElseThrow(
        () -> new NoSuchElementException("User with id " + userId + " does not exist")
    );
    Channel channel = channelRepository.findById(channelId).orElseThrow(
        () -> new NoSuchElementException("Channel with id " + channelId + " does not exist")
    );

    if (readStatusRepository.existsByChannelIdAndUserId(userId, channelId)) {
      throw new IllegalArgumentException(
          "ReadStatus already exists for user " + userId + " and channel " + channelId);
    }
    ReadStatus newReadStatus = ReadStatus.createReadStatus(user, channel, request.lastReadAt());

    readStatusRepository.save(newReadStatus);
    log.info("읽음 상태 생성 : {}", newReadStatus);
    return readStatusMapper.entityToDto(newReadStatus);
  }

  @Override
  public ReadStatusResponse find(UUID id) {
    return readStatusRepository.findById(id)
        .map(readStatusMapper::entityToDto)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
  }

  @Override
  public List<ReadStatusResponse> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::entityToDto)
        .toList();
  }

  @Override
  public ReadStatusResponse update(UUID id, ReadStatusRequest.Update request) {
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + id + " not found"));
    readStatus.updateLastReadAt(request.newLastReadAt());
    return readStatusMapper.entityToDto(readStatusRepository.save(readStatus));
  }

  @Override
  public void delete(UUID id) {
    if (!readStatusRepository.existsById(id)) {
      throw new NoSuchElementException("ReadStatus with id " + id + " not found");
    }
    readStatusRepository.deleteById(id);
  }
}
