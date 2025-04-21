package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
  void save(Message message);
  Message findById(UUID id);
  List<Message> findAll();
  void delete(UUID id);
}
