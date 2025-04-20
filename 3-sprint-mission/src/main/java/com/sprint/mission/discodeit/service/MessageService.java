package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(User user, Channel channel, String text);
    List<Message> readAll(User user, Channel channel);
    Message find(User user, Channel channel, UUID id);
    List<Message> findByText(User user, Channel channel, String text);
    void update(User user, Channel channel, UUID id, String text);
    void delete(User user, Channel channel, UUID id);

}
