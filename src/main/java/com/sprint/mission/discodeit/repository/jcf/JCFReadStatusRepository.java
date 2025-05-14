package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {


    private final Map<UUID, Set<ReadStatus>> readStatuses;

    public JCFReadStatusRepository() {
        this.readStatuses = new HashMap<>();
    }

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        ReadStatus readStatus = new ReadStatus(readStatusCreateRequestDto.getUserId(), readStatusCreateRequestDto.getChannelId());

        if (!readStatuses.containsKey(readStatusCreateRequestDto.getChannelId())) {
            Set<ReadStatus> statusSet = new HashSet<>();
            statusSet.add(readStatus);
            readStatuses.put(readStatusCreateRequestDto.getChannelId(), statusSet);
        }
        readStatuses.get(readStatus.getChannelId()).add(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        for (Set<ReadStatus> readStatusSet : readStatuses.values()) {
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
        List<ReadStatus> readStatuses = new ArrayList<>();
        for (Set<ReadStatus> readStatusSet : this.readStatuses.values()) {
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
        for (Map.Entry<UUID, Set<ReadStatus>> entry : readStatuses.entrySet()) {
            for (ReadStatus readStatus : entry.getValue()) {
                if (readStatus.getId().equals(readStatusUpdateRequestDto.getStatusId())) {
                    readStatus.setJoinedAt(readStatusUpdateRequestDto.getNowTime());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteReadStatus(UUID readStatusId) {
        Iterator<Map.Entry<UUID, Set<ReadStatus>>> mapIterator = readStatuses.entrySet().iterator();

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

                    return true;
                }
            }
        }

        return false;
    }
}
