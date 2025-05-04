package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, Map<UUID, ReadStatus>> readStatusMap = new HashMap<>();

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    readStatusMap.computeIfAbsent(readStatus.getChannelId(), channelId -> new HashMap<>())
        .put(readStatus.getUserId(), readStatus);
    return readStatus;
  }

  @Override
  public ReadStatus find(UUID userId, UUID channelId) {
    Map<UUID, ReadStatus> channelStatus = readStatusMap.get(channelId);
    return (channelStatus != null) ? channelStatus.get(userId) : null;
  }

  @Override
  public void updateLastReadAt(UUID userId, UUID channelId, Instant newReadAt) {
    ReadStatus readStatus = find(userId, channelId);
    if (readStatus != null) {
      readStatus.updateLastReadAt(newReadAt);
    }
  }

  @Override
  public List<ReadStatus> findByChannelId(UUID channelId) {
    return new ArrayList<>(readStatusMap.getOrDefault(channelId, Collections.emptyMap()).values());
  }

  @Override
  public void deleteAll(List<ReadStatus> readStatuses) {
    for (ReadStatus rs : readStatuses) {
      Map<UUID, ReadStatus> channelStatus = readStatusMap.get(rs.getChannelId());
      if (channelStatus != null) {
        channelStatus.remove(rs.getUserId());
        // 만약 Map이 비어 있으면 제거
        if (channelStatus.isEmpty()) {
          readStatusMap.remove(rs.getChannelId());
        }
      }
    }
  }
}

