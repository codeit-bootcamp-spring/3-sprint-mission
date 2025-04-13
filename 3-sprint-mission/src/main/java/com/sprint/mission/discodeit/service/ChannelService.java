package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(User user) throws IOException;
    Channel read(User user, UUID id) throws IOException;
    List<Channel> readAll(User user);
    void updateName(User user, UUID id) throws IOException;
    void delete(User user, UUID id);

}
