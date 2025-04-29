package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    void saveAll(List<Message> messages);

    List<Message> loadAll();

    Message loadById(UUID id);

    List<Message> loadByType(String type);

}
