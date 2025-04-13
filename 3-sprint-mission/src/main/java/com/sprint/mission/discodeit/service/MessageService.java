package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void create(String text, User user, Channel channel);
    Message read(UUID id);
    List<Message> readAll();
    void update(UUID id, String text);
    void delete(UUID id);

}
