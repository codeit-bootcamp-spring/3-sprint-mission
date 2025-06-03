package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author"})
  List<Message> findAllByChannelId(UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  // 커서 페이징
  @EntityGraph(attributePaths = {"author"})
  @Query("""
             SELECT m FROM Message m
             WHERE m.channel.id = :channelId
             AND m.createdAt < :cursor
             ORDER BY m.createdAt DESC
      """)
  List<Message> findByChannelIdAndCreatedBeforeOrderByCreatedAtDesc(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant createdAt,
      Pageable pageable
  );
}