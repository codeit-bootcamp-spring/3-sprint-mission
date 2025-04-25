package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileReadStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 26.        doungukkim       최초 생성
 */
@Primary
@Repository
@RequiredArgsConstructor
public class FileReadStatusRepository implements ReadStatusRepository {
    private final FilePathUtil filePathUtil;

    @Override
    public ReadStatus createReadStatusByUserId(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        Path path = filePathUtil.getReadStatusFilePath(readStatus.getId());
        FileSerializer.writeFile(path, readStatus);
        return readStatus;
    }

    @Override
    public List<ReadStatus> createReadStatusByUserId(List<UUID> userIds, UUID channelId) {
        List<ReadStatus> readStatusList = new ArrayList<>();
        for (UUID userId : userIds) {
            ReadStatus readStatus = new ReadStatus(userId, channelId);
            Path path = filePathUtil.getReadStatusFilePath(readStatus.getId());
            FileSerializer.writeFile(path, readStatus);
            readStatusList.add(readStatus);
        }
        return readStatusList;
    }
}
