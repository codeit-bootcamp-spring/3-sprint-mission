package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> storage = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        storage.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return storage.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByMessageId(UUID messageId) {
        return storage.values().stream()
                .filter(rs -> rs.getMessageId().equals(messageId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID readStatusId) {
        storage.remove(readStatusId);
    }
}