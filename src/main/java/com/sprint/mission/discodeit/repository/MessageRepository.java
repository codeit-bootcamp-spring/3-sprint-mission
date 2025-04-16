package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    public Message write(User user, Channel channel, String text);

    public Message read(UUID id);

    public List<Message> readAll();

    public boolean delete(UUID id);


}
