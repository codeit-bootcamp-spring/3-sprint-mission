package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    List<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId, Instant createdAt,
                                                                        Pageable pageable);

    void deleteAllByChannelId(UUID channelId);

    long countByChannelId(UUID channelId);
}