package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    // id = message ID
    Message create(UUID user, UUID channel, String text);
    List<Message> findAllByChannelId(UUID channel);
    Message find(UUID id);
    List<Message> findByText(String text);
    void update(UUID id, String text);
    void delete(UUID id) throws IOException;

}
