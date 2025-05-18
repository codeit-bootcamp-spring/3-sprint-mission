package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);
    List<Message> findAll();
    List<Message> findAllFromChannel(UUID channel);
    Message find(UUID id);
    List<Message> findByText(String text);
    void delete(UUID id) throws IOException;
}
