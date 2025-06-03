package com.sprint.mission.discodeit.repository.jpa;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
    List<Message> findAllById(UUID messageId);

    boolean existsById(UUID messageId);

//    List<Message> findAllByChannelIdOrderByCreatedAt(UUID channelId, Pageable pageable);

    Message findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

//    List<Message> findAllByChannelIdOrderByCreatedAt(UUID channelId, Pageable pageable);
    List<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    Page<Message> findAllPageByChannelIdOrderByCreatedAt(UUID channelId, Pageable pageable);

    List<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId, Instant cursor, Pageable pageable);

    Long countByChannelId(UUID channelId);

}
