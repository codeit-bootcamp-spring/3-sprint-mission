package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

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
    public Optional<ReadStatus> findByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = this.findAll();
        Optional<ReadStatus> readStatusStatusNullable = readStatuses.stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .findFirst();

        return readStatusStatusNullable;
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
