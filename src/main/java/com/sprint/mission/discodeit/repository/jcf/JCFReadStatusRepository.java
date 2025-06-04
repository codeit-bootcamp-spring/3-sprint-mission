package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private static final Map<UUID, ReadStatus> readStatuses = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) { return readStatuses.put(readStatus.getId(), readStatus); }

    @Override
    public Optional<ReadStatus> loadById(UUID id) { return Optional.ofNullable(readStatuses.get(id)); }

    @Override
    public List<ReadStatus> loadAllByUserId(UUID id) {
        return readStatuses.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(id)).toList();
    }

    @Override
    public List<ReadStatus> loadAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public void deleteByUserId(UUID userId) { readStatuses.remove(userId); }

    @Override
    public void deleteByChannelId(UUID channelId) {

    }

}
