package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message); // 저장 로직
    Optional<Message> findById(UUID messageId); // 저장 로직
    List<Message> findAll(); // 저장 로직
    void deleteById(UUID messageId); // 저장 로직
}
