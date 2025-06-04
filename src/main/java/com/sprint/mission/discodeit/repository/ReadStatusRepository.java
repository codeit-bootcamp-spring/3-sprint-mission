package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
//    @EntityGraph(attributePaths = {"user", "channel", "user.profile"})
    List<ReadStatus> findAllByUserId(UUID userId);
    void deleteAllByChannelId(UUID channelId);
//    @EntityGraph(attributePaths = {"user", "channel", "user.profile"})
    List<ReadStatus> findAllByChannelId(UUID channelId);
}
