package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  @Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.channel WHERE m.channel.id = :channelId")
  List<Message> findAllByChannelId(@Param("channelId") UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.channel WHERE m.channel.id=:channelId")
  Page<Message> findAllByChannelId(@Param("channelId") UUID channelId, Pageable pageable);


  @Query("SELECT DISTINCT m FROM Message m JOIN FETCH m.channel WHERE m.createdAt > :cursor AND m.channel.id=:channelId ORDER BY m.createdAt ASC")
  Page<Message> findAllByChannelId(@Param("channelId") UUID channelId, Instant cursor,
      Pageable pageable);
}
