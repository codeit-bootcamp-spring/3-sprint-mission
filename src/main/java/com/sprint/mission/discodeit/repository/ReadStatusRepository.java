package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findAllByUserId(UUID userId);

    void deleteAllByChannelId(UUID channelId);

    Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user"})
    List<ReadStatus> findAllByChannelId(UUID channelId);
}
