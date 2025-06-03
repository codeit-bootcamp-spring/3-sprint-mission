package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  @Query("SELECT m FROM Message m JOIN FETCH m.author JOIN FETCH m.channel WHERE m.channel.id = :channelId")
  Page<Message> findAllWithAuthorAndChannelByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable);

  void deleteAllByChannelId(UUID channelId);

  Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

  Slice<Message> findAllByChannel_IdAndCreatedAtBefore(UUID channelId, Instant createdAtBefore,
      Pageable pageable);

  Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);


}
