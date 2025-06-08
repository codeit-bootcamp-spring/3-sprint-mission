package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.awt.print.Pageable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Message save(Message message);
    Optional<Message> findById(UUID id);

    @Query("SELECT m FROM Message m "
        + "LEFT JOIN FETCH m.author a "
        + "JOIN FETCH a.status "
        + "LEFT JOIN FETCH a.profile "
        + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt"
    )
    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId, @Param("createdAt") Instant createdAt, Pageable pageable);

    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);
}