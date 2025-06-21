package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "AND m.createdAt < :createdAt "
      + "ORDER BY m.createdAt DESC ")
  Slice<Message> findAllByChannelId(
      @Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable
  );

  @Query("SELECT m FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "AND m.content LIKE %:content%")
  Slice<Message> findAllByChannelIdAndContent(
      @Param("channelId") UUID channelId,
      @Param("content") String content,
      @Param("createdAt") Instant createdAt,
      Pageable pageable
  );

  @Query("SELECT MAX(m.createdAt) FROM Message m "
      + "WHERE m.channel.id = :channelId")
  Optional<Instant> findLastMessageAtByChannelId(
      @Param("channelId") UUID channelId
  );

  void deleteAllByChannelId(UUID channelId);
}

