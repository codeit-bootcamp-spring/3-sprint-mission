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
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannel_Id(UUID channelId);

  void deleteAllByChannel_Id(UUID channelId);

  @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
  Optional<Instant> findLastMessageTimeByChannelId(@Param("channelId") UUID channelId);

  Page<Message> findAllByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

}
