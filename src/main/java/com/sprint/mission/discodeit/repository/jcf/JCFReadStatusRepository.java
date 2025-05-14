package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data; //database

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(this.data.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return this.findAll().stream().filter((readStatus) -> readStatus.getChannelId().equals(channelId)).toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        return this.data.containsKey(readStatusId);
    }

    @Override
    public void deleteById(UUID readStatusId) {
        this.data.remove(readStatusId);
    }
}
