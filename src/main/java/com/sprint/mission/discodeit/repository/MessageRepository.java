package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  @Query("SELECT m FROM Message m JOIN FETCH m.channel")
  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);
}
