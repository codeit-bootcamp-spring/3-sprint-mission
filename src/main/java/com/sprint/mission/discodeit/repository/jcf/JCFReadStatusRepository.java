package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> store = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        store.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return store.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return store.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return store.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        store.values().removeIf(rs -> rs.getChannelId().equals(channelId));
    }
}
