package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entitiy.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    public ReadStatus save(ReadStatus readStatus);
    public List<ReadStatus> read();
    public Optional<ReadStatus> readById(UUID id);
    public List<ReadStatus> readByUserId(UUID userId);
    public List<ReadStatus> readByChannelId(UUID channelId);
    public void update(UUID id, ReadStatus readStatus);
    public void delete(UUID readStatusId);
}
