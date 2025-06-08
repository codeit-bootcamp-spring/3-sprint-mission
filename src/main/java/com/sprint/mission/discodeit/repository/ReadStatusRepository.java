package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "channel"})
    List<ReadStatus> findByChannelId(UUID channelId);

    void deleteById(UUID id);

    @Modifying
    @Query("DELETE FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    void deleteByChannelId(@Param("channelId") UUID channelId);
}
