package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "channel"})
    @Query("SELECT m FROM Message m " +
            "WHERE m.channel.id = :channelId " +
            "AND (:cursor IS NULL OR m.createdAt < :cursor) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findPageByChannelIdAndCursor(@Param("channelId") UUID channelId,
                                               @Param("cursor") Instant cursor,
                                               Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    List<Message> findPageByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "channel"})
    Slice<Message> findSliceByChannelId(UUID channelId, Pageable pageable);
}
