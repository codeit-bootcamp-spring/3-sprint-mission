package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @NonNull
  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  Optional<Message> findById(@NonNull UUID messageId);

  @EntityGraph(attributePaths = {"author", "channel", "attachments"})
  List<Message> findAllByChannelId(UUID channelId);

  @Query("SELECT m.id FROM Message m WHERE m.channel.id = :channelId")
  List<UUID> findMessageIdsByChannelId(@Param("channelId") UUID channelId);

  @Query("""
      SELECT MAX(m.createdAt)
            FROM Message m
            WHERE m.channel.id = :channelId
      """)
  Instant findLastCreatedAtByChannelId(@NonNull UUID channelId);

  Page<Message> findByChannelIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID channelId,
      Instant createdAt, Pageable pageable);

  void deleteById(@NonNull UUID messageId);

  void deleteByChannelId(@NonNull UUID channelId);
}
