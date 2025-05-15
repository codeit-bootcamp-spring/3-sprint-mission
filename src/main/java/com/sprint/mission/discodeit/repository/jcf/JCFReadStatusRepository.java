package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private static volatile JCFReadStatusRepository instance;
    private final Map<UUID, ReadStatus> readStatuses = new ConcurrentHashMap<>();

    private JCFReadStatusRepository() {}

    public static JCFReadStatusRepository getInstance() {
        JCFReadStatusRepository result = instance;
        if (result == null) {
            synchronized (JCFReadStatusRepository.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JCFReadStatusRepository();
                }
            }
        }
        return result;
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatuses.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(readStatuses.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatuses.values().stream()
                .filter(status -> status.getUserId().equals(userId)) // Assuming ReadStatus entity has getUserId
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return readStatuses.values().stream()
                .filter(status -> status.getChannelId().equals(channelId)) // Assuming ReadStatus entity has getChannelId
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return readStatuses.values().stream()
                .filter(status -> status.getUserId().equals(userId) && status.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID id) {
        return readStatuses.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        readStatuses.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        readStatuses.values().removeIf(status -> status.getChannelId().equals(channelId));
    }
}