package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {

  // 기본 저장소: ID -> ReadStatus 매핑
  private final Map<UUID, ReadStatus> readStatusMap = new ConcurrentHashMap<>();

  // 사용자 ID + 채널 ID -> ReadStatus ID 매핑 (복합 키 조회용)
  private final Map<String, UUID> userChannelToStatusIdMap = new ConcurrentHashMap<>();

  // 채널 ID별 ReadStatus ID 목록
  private final Map<UUID, List<UUID>> channelToStatusIdsMap = new ConcurrentHashMap<>();

  // 사용자 ID별 ReadStatus ID 목록
  private final Map<UUID, List<UUID>> userToStatusIdsMap = new ConcurrentHashMap<>();

  @Override
  public void insert(ReadStatus readStatus) {
    // 중복된 ReadStatus가 존재하면 예외 발생
    Optional<ReadStatus> existing = findByUserIdAndChannelId(readStatus.getUserId(),
        readStatus.getChannelId());
    if (existing.isPresent()) {
      throw new IllegalArgumentException(
          "이미 존재하는 읽기 상태입니다. [UserID: " + readStatus.getUserId() + ", ChannelID: "
              + readStatus.getChannelId() + "]");
    }
    readStatusMap.put(readStatus.getId(), readStatus);
    String compositeKey = createCompositeKey(readStatus.getUserId(), readStatus.getChannelId());
    userChannelToStatusIdMap.put(compositeKey, readStatus.getId());
    channelToStatusIdsMap
        .computeIfAbsent(readStatus.getChannelId(), k -> new ArrayList<>())
        .add(readStatus.getId());
    userToStatusIdsMap
        .computeIfAbsent(readStatus.getUserId(), k -> new ArrayList<>())
        .add(readStatus.getId());
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(readStatusMap.get(id));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    // userId와 channelId를 기준으로 ReadStatus 조회
    return findAll().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId()
            .equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return userToStatusIdsMap.getOrDefault(userId, List.of()).stream()
        .map(this::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return channelToStatusIdsMap.getOrDefault(channelId, List.of()).stream()
        .map(this::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findAll() {
    return new ArrayList<>(readStatusMap.values());
  }

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    Optional<ReadStatus> existing = findById(readStatus.getId());
    if (existing.isPresent()) {
      readStatusMap.put(readStatus.getId(), readStatus);
    } else {
      readStatusMap.put(readStatus.getId(), readStatus);
    }
    return readStatus;
  }

  @Override
  public void update(ReadStatus readStatus) {
    Optional<ReadStatus> existing = findById(readStatus.getId());
    if (existing.isEmpty()) {
      throw new IllegalArgumentException(
          "존재하지 않는 읽기 상태입니다. [ReadStatusID: " + readStatus.getId() + "]");
    }
    readStatusMap.put(readStatus.getId(), readStatus);
  }

  @Override
  public void delete(UUID id) {
    findById(id).ifPresent(status -> {
      readStatusMap.remove(id);

      // 복합 키 매핑에서 삭제
      String compositeKey = createCompositeKey(status.getUserId(), status.getChannelId());
      userChannelToStatusIdMap.remove(compositeKey);

      // 채널 ID 매핑에서 삭제
      channelToStatusIdsMap.computeIfPresent(status.getChannelId(),
          (k, v) -> {
            v.remove(id);
            return v.isEmpty() ? null : v;
          });

      // 사용자 ID 매핑에서 삭제
      userToStatusIdsMap.computeIfPresent(status.getUserId(),
          (k, v) -> {
            v.remove(id);
            return v.isEmpty() ? null : v;
          });
    });
  }

  private String createCompositeKey(UUID userId, UUID channelId) {
    return userId.toString() + ":" + channelId.toString();
  }
}
