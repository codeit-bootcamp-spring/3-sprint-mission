package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;

public interface MessageService {
    void create(String text, User user, Channel channel);
    List<Message> read(String id);
    List<Message> readAll();
    void update(String id, String text);
    void delete(String id);

}
