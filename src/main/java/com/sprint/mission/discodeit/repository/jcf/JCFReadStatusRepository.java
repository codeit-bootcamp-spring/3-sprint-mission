package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {


    private final Map<UUID, Set<ReadStatus>> readStatuses;

    public JCFReadStatusRepository() {
        this.readStatuses = new HashMap<>();
    }

    @Override
    public ReadStatus createReadStatus(ReadStatus readStatus) {
        Set<ReadStatus> statusSet = readStatuses.computeIfAbsent(
                readStatus.getChannelId(),
                k -> new HashSet<>()
        );
        statusSet.add(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findReadStatusById(UUID readStatusId) {
        return readStatuses.values().stream()
                .flatMap(Set::stream)
                .filter(status -> status.getId().equals(readStatusId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatuses.values().stream()
                .flatMap(Set::stream)
                .filter(status -> status.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateReadStatus(ReadStatus readStatus) {
        boolean updated = false;

        for (Set<ReadStatus> statusSet : readStatuses.values()) {
            if (statusSet.removeIf(rs -> rs.getId().equals(readStatus.getId()))) {
                statusSet.add(readStatus);
                updated = true;
                break;
            }
        }

        return updated;
    }

    @Override
    public boolean deleteReadStatus(UUID statusId) {
        for (Iterator<Map.Entry<UUID, Set<ReadStatus>>> mapIterator = readStatuses.entrySet().iterator();
             mapIterator.hasNext(); ) {
            Map.Entry<UUID, Set<ReadStatus>> entry = mapIterator.next();
            Set<ReadStatus> statusSet = entry.getValue();

            if (statusSet.removeIf(status -> status.getId().equals(statusId))) {
                if (statusSet.isEmpty()) {
                    mapIterator.remove();
                }
                return true;
            }
        }

        return false;
    }

}
