package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
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
@Profile("jcf")
public class jcfReadStatusRepository implements ReadStatusRepository{

    Map<UUID, ReadStatus> data = new HashMap<>();


    @Override
    public void updateUpdatedTime(UUID readStatusId, Instant newTime) {
        ReadStatus readStatus = data.get(readStatusId);
        if (readStatus != null) {
            readStatus.setUpdatedAt(newTime);
        }
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return data.get(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values()
                .stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void deleteReadStatusById(UUID readStatusId) {
        data.remove(readStatusId);
    }

    @Override
    public ReadStatus createByUserId(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> createByUserId(List<UUID> userIds, UUID channelId) {
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

    @Override
    public List<ReadStatus> findReadStatusesByChannelId(UUID channelId) {
        List<ReadStatus> readStatusList = data.values()
                .stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
        return readStatusList;
    }
}
