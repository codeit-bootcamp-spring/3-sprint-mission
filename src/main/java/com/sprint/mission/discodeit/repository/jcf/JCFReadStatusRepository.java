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
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
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
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(readStatusMap.get(id));
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    String compositeKey = createCompositeKey(userId, channelId);
    return Optional.ofNullable(userChannelToStatusIdMap.get(compositeKey))
        .flatMap(this::findById);
  }

  @Override
  public List<ReadStatus> findByUserId(UUID userId) {
    return userToStatusIdsMap.getOrDefault(userId, List.of()).stream()
        .map(this::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
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
    // 기존 데이터가 있는 경우 삭제
    if (readStatus.getId() != null) {
      deleteById(readStatus.getId());
    }

    // 새 객체 생성 (ID가 없는 경우)
    ReadStatus toSave = (readStatus.getId() == null)
        ? ReadStatus.create(readStatus.getUserId(), readStatus.getChannelId())
        : readStatus;

    // 저장 로직
    readStatusMap.put(toSave.getId(), toSave);
    String compositeKey = createCompositeKey(toSave.getUserId(), toSave.getChannelId());
    userChannelToStatusIdMap.put(compositeKey, toSave.getId());
    channelToStatusIdsMap
        .computeIfAbsent(toSave.getChannelId(), k -> new ArrayList<>())
        .add(toSave.getId());
    userToStatusIdsMap
        .computeIfAbsent(toSave.getUserId(), k -> new ArrayList<>())
        .add(toSave.getId());

    return toSave;
  }

  @Override
  public void deleteById(UUID id) {
    findById(id).ifPresent(status -> {
      // 기본 저장소에서 삭제
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