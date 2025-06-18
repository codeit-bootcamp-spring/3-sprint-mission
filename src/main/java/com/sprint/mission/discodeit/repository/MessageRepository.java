package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "channel"})
    List<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
            @Param("channelId") UUID channelId,
            @Param("createdAt") Instant createdAt,
            Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    List<Message> findPageByChannelId(UUID channelId, Pageable pageable);
}
