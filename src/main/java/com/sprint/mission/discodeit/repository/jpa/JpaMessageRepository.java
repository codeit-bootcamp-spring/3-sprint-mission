package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : JpqMessageRepository
 * Author       : dounguk
 * Date         : 2025. 5. 28.
 */
public interface JpaMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannelId(UUID channelId);

    boolean existsById(UUID messageId);

    Message findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

    @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId AND (:cursor IS NULL OR m.createdAt < :cursor) ORDER BY m.createdAt DESC")
    List<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId, Instant cursor, Pageable pageable);

    Long countByChannelId(UUID channelId);
}
