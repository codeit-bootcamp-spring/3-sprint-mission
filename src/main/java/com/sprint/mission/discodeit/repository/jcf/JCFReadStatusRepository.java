package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Profile("jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private static final Map<UUID, ReadStatus> readStatuses = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) { readStatuses.put(readStatus.getId(), readStatus); }

    @Override
    public ReadStatus loadById(UUID id) { return readStatuses.get(id); }

    @Override
    public List<ReadStatus> loadAllByUserId(UUID id) {
        return readStatuses.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(id)).toList();
    }

    @Override
    public void deleteByUserId(UUID userId) { readStatuses.remove(userId); }

}
