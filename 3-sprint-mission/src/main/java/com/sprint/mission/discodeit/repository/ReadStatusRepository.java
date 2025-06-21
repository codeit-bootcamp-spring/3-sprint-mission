package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByChannelId(UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
//    boolean existsByUserId(UUID userId);
    void deleteAllByChannelId(UUID channelId);
}
