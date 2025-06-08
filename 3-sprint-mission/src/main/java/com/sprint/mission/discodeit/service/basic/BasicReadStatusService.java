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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
  public ReadStatus create(ReadStatusCreateRequest request) {
    User user = request.user();
    Channel channel = request.channel();

    if (!userRepository.existsById(user.getId())) {
      throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
    }
    if (!channelRepository.existsById(channel.getId())) {
      throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
    }
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

    return readStatusRepository.save(readStatus);
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
    System.out.println("Service의 findAllByUserId 메서드 호출됨!!");
    System.out.println("userId = " + userId);

    findAll().forEach(System.out::println);

    return readStatusRepository.findAllByUserId(userId).stream()
            .map(readStatusMapper::toDTO)
        .toList();
  }

  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.lastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
    readStatus.update(newLastReadAt);
    return readStatus;
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
