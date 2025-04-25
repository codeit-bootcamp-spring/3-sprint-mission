package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
    //    Instant readingTime(UUID readStatusId);
//
//    UUID saveReadStatus(UUID userId, UUID channelId);
//
//    ReadStatus findReadStatusById(UUID readStatusId);
//
//    ReadStatus findReadStatusByUserId(UUID userId);
//
//    ReadStatus findReadStatusByChannelId(UUID channelId);
//
//    void deleteReadStatus(UUID readStatusId);
    ReadStatus createReadStatusByUserId(UUID userId, UUID channelId);
    List<ReadStatus> createReadStatusByUserId(List<UUID> userIds, UUID channelId);


}
