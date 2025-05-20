package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository extends AbstractFileRepository<UUID, Set<ReadStatus>> implements ReadStatusRepository {

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/readStatus.ser");
    }

    @Override
    public ReadStatus createReadStatus(ReadStatus readStatus) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        Set<ReadStatus> statusSet = statuses.computeIfAbsent(
                readStatus.getChannelId(),
                k -> new HashSet<>()
        );
        statusSet.add(readStatus);
        saveToFile(statuses);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findReadStatusById(UUID readStatusId) {
        return loadFromFile().values().stream()
                .flatMap(Set::stream)
                .filter(status -> status.getId().equals(readStatusId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return loadFromFile().values().stream()
                .flatMap(Set::stream)
                .filter(status -> status.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateReadStatus(ReadStatus readStatus) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        boolean updated = false;

        for (Set<ReadStatus> statusSet : statuses.values()) {
            if (statusSet.removeIf(rs -> rs.getId().equals(readStatus.getId()))) {
                statusSet.add(readStatus);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveToFile(statuses);
        }
        return updated;
    }


    @Override
    public boolean deleteReadStatus(UUID statusId) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        boolean deleted = false;

        for (Set<ReadStatus> statusSet : statuses.values()) {
            if (statusSet.removeIf(status -> status.getId().equals(statusId))) {
                deleted = true;
                break;
            }
        }

        if (deleted) {
            saveToFile(statuses);
        }
        return deleted;
    }

}
