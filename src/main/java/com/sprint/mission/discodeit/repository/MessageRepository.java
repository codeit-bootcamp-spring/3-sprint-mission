package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.channel WHERE m.channel.id = :channelId")
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.channel WHERE m.createdAt > :cursor AND m.channel.id=:channelId ORDER BY m.createdAt ASC")
  Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

  boolean existsByIdAndAuthor_Id(UUID messageId, UUID authorId);
}
