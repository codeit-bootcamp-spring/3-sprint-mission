package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(User user) throws IOException;
    Channel find(User user, UUID id) throws IOException;
    List<Channel> findByName(User user, String name);
    List<Channel> findAll(User user);
    void addEntry(User user, UUID id, UUID entryId);
    Channel enterChannel(User user, UUID id) throws IOException;
    void updateName(User user, UUID id) throws IOException;
    void delete(User user, UUID id);

}
