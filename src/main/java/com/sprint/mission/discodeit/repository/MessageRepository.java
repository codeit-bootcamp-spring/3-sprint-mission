package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.awt.print.Pageable;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @Query("SELECT m.createdAt "
        + "FROM Message m "
        + "WHERE m.channel.id = :channelId "

        + "ORDER BY m.createdAt DESC LIMIT 1")
    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}