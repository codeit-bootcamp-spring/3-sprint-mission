package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        data = new HashMap<>();
    }

    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        Optional<ReadStatus> foundReadStatus = data.values().stream()
                .filter(readStatus ->
                    readStatus.getChannelId().equals(channelId) && readStatus.getUserId().equals(userId)
                )
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Optional<ReadStatus> foundReadStatus = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId));
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId)
        && entry.getValue().getUserId().equals(userId));
    }
}
