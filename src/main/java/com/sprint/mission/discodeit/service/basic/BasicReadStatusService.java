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
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    // N+1 문제 해결: 효율적인 쿼리로 중복 체크
    boolean alreadyExists = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
    if (alreadyExists) {
      throw DuplicateReadStatusException.withUserIdAndChannelId(userId, channelId);
    }

    // 연관 엔티티들을 로드
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withUserId(userId));

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withChannelId(channelId));

    Instant lastReadAt = request.lastReadAt();
    // 엔티티 연관관계를 활용한 ReadStatus 생성
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

    return readStatusRepository.save(readStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatus find(UUID readStatusId) {
    // N+1 문제 해결: 연관 엔티티를 Fetch Join으로 한 번에 조회
    return readStatusRepository.findByIdWithUserAndChannel(readStatusId)
        .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(readStatusId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    // N+1 문제 해결: 사용자와 채널 정보를 모두 Fetch Join으로 한 번에 조회
    return readStatusRepository.findAllByUserIdWithUserAndChannel(userId);
  }

  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();

    // N+1 문제 해결: 연관 엔티티를 Fetch Join으로 한 번에 조회
    ReadStatus readStatus = readStatusRepository.findByIdWithUserAndChannel(readStatusId)
        .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(readStatusId));

    // 변경 감지(Dirty Checking) 활용 - save() 호출 불필요
    readStatus.update(newLastReadAt);

    return readStatus; // 트랜잭션 커밋 시 자동으로 변경 사항 반영
  }

  @Override
  public void delete(UUID readStatusId) {
    // N+1 문제 해결: 연관 엔티티를 Fetch Join으로 한 번에 조회
    ReadStatus readStatus = readStatusRepository.findByIdWithUserAndChannel(readStatusId)
        .orElseThrow(() -> ReadStatusNotFoundException.withReadStatusId(readStatusId));

    readStatusRepository.delete(readStatus);
  }
}
