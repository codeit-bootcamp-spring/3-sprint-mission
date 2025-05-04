package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    ReadStatus findById(UUID id);
    List<ReadStatus> findByChannelId(UUID channelId);
    List<ReadStatus> findAll();
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findByUserId(UUID userId); // UserId에 해당하는 채널별 ReadStatus 불러오기
    void delete(UUID id);
    boolean existByChannelIdAndUserId(UUID userId, UUID channelId);
    ReadStatus update(ReadStatus readStatus);
}
