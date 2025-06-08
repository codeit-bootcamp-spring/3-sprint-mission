package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m JOIN FETCH m.channel")
  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}
