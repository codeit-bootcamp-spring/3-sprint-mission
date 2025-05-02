package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository extends AbstractFileRepository<UUID, Set<ReadStatus>> implements ReadStatusRepository {

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/readStatus.ser");
    }

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        ReadStatus readStatus = new ReadStatus(readStatusCreateRequestDto.getUserId(), readStatusCreateRequestDto.getChannelId());
        readStatus.setJoinedAt(Instant.now());
        if (!statuses.containsKey(readStatusCreateRequestDto.getChannelId())) {
            Set<ReadStatus> statusSet = new HashSet<>();
            statusSet.add(readStatus);
            statuses.put(readStatusCreateRequestDto.getChannelId(), statusSet);
            saveToFile(statuses);
            return readStatus;
        }
        statuses.get(readStatus.getChannelId()).add(readStatus);
        saveToFile(statuses);
        return readStatus;
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        for (Set<ReadStatus> readStatusSet : statuses.values()) {
            for (ReadStatus readStatus : readStatusSet) {
                if (readStatus.getId().equals(readStatusId)) {
                    return readStatus;
                }
            }
        }
        throw new NoSuchElementException("존재하지 않는 데이터 입니다.");
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (Set<ReadStatus> readStatusSet : statuses.values()) {
            for (ReadStatus readStatus : readStatusSet) {
                if (readStatus.getUserId().equals(userId)) {
                    readStatuses.add(readStatus);
                }
            }
        }
        return readStatuses;
    }

    @Override
    public boolean updateReadStatus(ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        for (Map.Entry<UUID, Set<ReadStatus>> entry : statuses.entrySet()) {
            for (ReadStatus readStatus : entry.getValue()) {
                if (readStatus.getId().equals(readStatusUpdateRequestDto.getStatusId())) {
                    readStatus.setJoinedAt(readStatusUpdateRequestDto.getNowTime());
                    saveToFile(statuses);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteReadStatus(UUID readStatusId) {
        Map<UUID, Set<ReadStatus>> statuses = loadFromFile();
        Iterator<Map.Entry<UUID, Set<ReadStatus>>> mapIterator = statuses.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<UUID, Set<ReadStatus>> entry = mapIterator.next();
            Set<ReadStatus> readStatusSet = entry.getValue();

            Iterator<ReadStatus> setIterator = readStatusSet.iterator();
            while (setIterator.hasNext()) {
                ReadStatus readStatus = setIterator.next();
                if (readStatus.getId().equals(readStatusId)) {
                    setIterator.remove();

                    // Value 비었으면 Key도 같이 삭제
                    if (readStatusSet.isEmpty()) {
                        mapIterator.remove();
                    }

                    saveToFile(statuses);
                    return true;
                }
            }
        }
        return false;
    }
}
