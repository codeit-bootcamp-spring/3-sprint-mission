package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : jcfReadStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 26.        doungukkim       최초 생성
 */
//@Primary
@Repository
public class jcfReadStatusRepository implements ReadStatusRepository{
    Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus createReadStatusByUserId(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> createReadStatusByUserId(List<UUID> userIds, UUID channelId) {
        Map<UUID, ReadStatus> localData = new HashMap<>();
        List<ReadStatus> readStatusList = new ArrayList<>();
        for (UUID userId : userIds) {
            ReadStatus readStatus = new ReadStatus(userId, channelId);
            localData.put(readStatus.getId(), readStatus);
            readStatusList.add(readStatus);
        }
        data.putAll(localData);
        return readStatusList;
    }
}
