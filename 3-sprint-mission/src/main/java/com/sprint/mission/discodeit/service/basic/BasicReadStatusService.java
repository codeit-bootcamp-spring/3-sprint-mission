package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ReadStatusDTO create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("해당 사용자가 존재하지 않습니다."));

    if (readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .anyMatch(readStatus -> readStatus.getUser().equals(user))) {
      throw new IllegalArgumentException("이미 존재하는 데이터입니다.");
    }

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus =
        ReadStatus.builder()
            .user(user)
            .channel(channel)
            .lastReadAt(lastReadAt)
            .build();
    readStatusRepository.save(readStatus);
    return readStatusMapper.toDTO(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDTO find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
  }

  @Transactional(readOnly = true)
  public List<ReadStatusDTO> findAll() {
    return readStatusRepository.findAll().stream()
        .map(readStatusMapper::toDTO)
        .toList();

  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDTO> findAllByUserId(UUID userId) {
    findAll().forEach(System.out::println);

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDTO)
        .toList();
  }

  @Override
  public ReadStatusDTO update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.lastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
    readStatus.update(newLastReadAt);
    return readStatusMapper.toDTO(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
