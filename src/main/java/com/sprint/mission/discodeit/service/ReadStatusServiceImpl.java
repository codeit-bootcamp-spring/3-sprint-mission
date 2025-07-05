package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.common.exception.ReadStatusException;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    // 유저 존재 확인
    userRepository.findById(request.userId())
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(request.userId(), request.channelId()));

    // 채널 존재 확인
    channelRepository.findById(request.channelId())
        .orElseThrow(
            () -> ReadStatusException.invalidUserOrChannel(request.userId(), request.channelId()));

    // 중복 체크
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
    return readStatusRepository.findByUserId(userId);
  }

  @Override
  public ReadStatus update(UUID id) {
    // ReadStatus 조회
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> ReadStatusException.notFound(id));

    // lastReadAt 업데이트
    readStatus.updateLastReadAt();
    return readStatusRepository.save(readStatus);
  }

  @Override
  public void delete(UUID id) {
    // 삭제 전에 존재하는지 확인
    if (readStatusRepository.findById(id).isEmpty()) {
      throw ReadStatusException.notFound(id);
    }
    readStatusRepository.delete(id);
  }
}
