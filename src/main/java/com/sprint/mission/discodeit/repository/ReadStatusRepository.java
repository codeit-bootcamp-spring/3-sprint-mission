package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findAllByChannelId(UUID channelId);

    boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
