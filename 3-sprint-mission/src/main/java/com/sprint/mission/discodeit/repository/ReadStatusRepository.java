package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository {

    ReadStatus save(ReadStatus readStatus);
    List<ReadStatus> findAll();
    ReadStatus find(UUID id);
    void delete(UUID id) throws IOException;
}
