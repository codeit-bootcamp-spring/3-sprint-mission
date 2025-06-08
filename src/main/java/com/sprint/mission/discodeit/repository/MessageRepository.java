package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  void deleteAllByChannelId(UUID channelId);
}
