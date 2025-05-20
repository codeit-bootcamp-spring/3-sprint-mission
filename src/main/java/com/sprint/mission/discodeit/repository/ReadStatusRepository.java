package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository
 * fileName       : ReadStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 23.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 23.        doungukkim       최초 생성
 */
// 채널별로 객체 생성
public interface ReadStatusRepository {

    ReadStatus createByUserId(UUID userId, UUID channelId, Instant lastReadAt);

    List<ReadStatus> createByUserId(List<UUID> userIds, UUID channelId);

    List<ReadStatus> findReadStatusesByChannelId(UUID channelId); // emptyList

    void deleteReadStatusById(UUID readStatusId); // file, jcf : throw exception

    ReadStatus findById(UUID readStatusId); //null

    List<ReadStatus> findAllByUserId(UUID userId);

    void updateUpdatedTime(UUID userId, Instant newLastReadAt);


}
