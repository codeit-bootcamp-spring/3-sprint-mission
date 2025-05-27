package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository {
    Message save(Message message);
    Message findById(UUID id);
    List<Message> findByChannel(UUID channelId);
    List<Message> findAll();
    boolean existsById(UUID id);
    void delete(UUID id);
    void deleteByChannelId(UUID channelId);
}