package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);
    List<Message> findAll();
    List<Message> findAllByChannelId(UUID channelId);
    Optional<Message> findById(UUID id);
    List<Message> findByText(String text);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId);
}
