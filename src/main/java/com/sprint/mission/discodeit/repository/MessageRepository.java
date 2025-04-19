package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    public Message write(Message message);

    public Message read(UUID messageId);

    public List<Message> readAll();

    public void delete(UUID messageId);


}
